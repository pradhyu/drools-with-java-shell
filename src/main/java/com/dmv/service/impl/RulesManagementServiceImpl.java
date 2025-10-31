package com.dmv.service.impl;

import com.dmv.model.*;
import com.dmv.service.RulesManagementService;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RulesManagementServiceImpl implements RulesManagementService {

    private static final Logger logger = LoggerFactory.getLogger(RulesManagementServiceImpl.class);

    private final KieServices kieServices;
    private final AtomicReference<KieContainer> kieContainerRef;
    private final com.dmv.service.ExternalDataService externalDataService;

    @Autowired
    public RulesManagementServiceImpl(KieServices kieServices, KieContainer kieContainer,
                                     com.dmv.service.ExternalDataService externalDataService) {
        this.kieServices = kieServices;
        this.kieContainerRef = new AtomicReference<>(kieContainer);
        this.externalDataService = externalDataService;
    }

    @Override
    public RuleCompilationResult compileRule(String ruleContent) {
        logger.debug("Compiling rule content: {}", ruleContent.substring(0, Math.min(100, ruleContent.length())));
        
        RuleCompilationResult result = new RuleCompilationResult();
        
        try {
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            kieFileSystem.write("src/main/resources/temp-rule.drl", ruleContent);
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                for (Message message : kieBuilder.getResults().getMessages(Message.Level.ERROR)) {
                    result.addError(message.getText());
                }
                logger.warn("Rule compilation failed with errors: {}", result.getErrors());
            } else {
                result.setSuccess(true);
                logger.debug("Rule compiled successfully");
            }
            
            // Add warnings if any
            for (Message message : kieBuilder.getResults().getMessages(Message.Level.WARNING)) {
                result.addWarning(message.getText());
            }
            
        } catch (Exception e) {
            result.addError("Compilation exception: " + e.getMessage());
            logger.error("Exception during rule compilation", e);
        }
        
        return result;
    }

    @Override
    public void deployRule(String ruleName, String ruleContent) {
        logger.info("Deploying rule: {}", ruleName);
        
        RuleCompilationResult compilationResult = compileRule(ruleContent);
        if (!compilationResult.isSuccess()) {
            throw new RuntimeException("Cannot deploy rule with compilation errors: " + compilationResult.getErrors());
        }
        
        try {
            // Create new KieFileSystem with existing rules plus the new one
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            
            // Load existing rules
            reloadExistingRules(kieFileSystem);
            
            // Add the new rule
            kieFileSystem.write("src/main/resources/rules/" + ruleName + ".drl", ruleContent);
            
            // Build and deploy
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                throw new RuntimeException("Rule deployment failed: " + kieBuilder.getResults().getMessages());
            }
            
            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer newContainer = kieServices.newKieContainer(kieModule.getReleaseId());
            
            // Atomically replace the container
            kieContainerRef.set(newContainer);
            
            logger.info("Rule '{}' deployed successfully", ruleName);
            
        } catch (Exception e) {
            logger.error("Failed to deploy rule: {}", ruleName, e);
            throw new RuntimeException("Rule deployment failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void reloadAllRules() {
        logger.info("Reloading all rules from classpath...");
        
        try {
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            reloadExistingRules(kieFileSystem);
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                throw new RuntimeException("Rule reload failed: " + kieBuilder.getResults().getMessages());
            }
            
            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer newContainer = kieServices.newKieContainer(kieModule.getReleaseId());
            
            kieContainerRef.set(newContainer);
            
            logger.info("All rules reloaded successfully");
            
        } catch (Exception e) {
            logger.error("Failed to reload rules", e);
            throw new RuntimeException("Rule reload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RuleMetadata> getLoadedRules() {
        List<RuleMetadata> ruleMetadataList = new ArrayList<>();
        
        KieContainer container = kieContainerRef.get();
        Collection<KiePackage> kiePackages = container.getKieBase().getKiePackages();
        
        for (KiePackage kiePackage : kiePackages) {
            for (Rule rule : kiePackage.getRules()) {
                RuleMetadata metadata = new RuleMetadata();
                metadata.setRuleName(rule.getName());
                metadata.setPackageName(rule.getPackageName());
                metadata.setLastModified(LocalDateTime.now());
                metadata.setStatus(RuleStatus.ACTIVE);
                
                ruleMetadataList.add(metadata);
            }
        }
        
        logger.debug("Retrieved metadata for {} rules", ruleMetadataList.size());
        return ruleMetadataList;
    }

    @Override
    public RuleExecutionResult executeRules(List<Object> facts) {
        logger.debug("Executing rules against {} facts", facts.size());
        
        RuleExecutionResult result = new RuleExecutionResult();
        long startTime = System.currentTimeMillis();
        
        try {
            KieContainer container = kieContainerRef.get();
            KieSession kieSession = container.newKieSession();
            
            // Register global variables
            kieSession.setGlobal("externalDataService", externalDataService);
            
            // Add event listener to track rule firings and fact modifications
            kieSession.addEventListener(new DefaultRuleRuntimeEventListener() {
                @Override
                public void objectInserted(ObjectInsertedEvent event) {
                    result.addModifiedFact(event.getObject());
                }
                
                @Override
                public void objectUpdated(ObjectUpdatedEvent event) {
                    result.addModifiedFact(event.getObject());
                }
            });
            
            // Insert facts
            for (Object fact : facts) {
                kieSession.insert(fact);
            }
            
            // Fire rules
            int rulesFired = kieSession.fireAllRules();
            result.setRulesFired(rulesFired);
            result.setSuccess(true);
            
            // Collect all facts (including modified ones)
            Collection<Object> allFacts = new ArrayList<>();
            for (Object fact : kieSession.getObjects()) {
                allFacts.add(fact);
            }
            result.setModifiedFacts(new ArrayList<>(allFacts));
            
            kieSession.dispose();
            
            logger.debug("Rules execution completed. {} rules fired", rulesFired);
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Rule execution failed: " + e.getMessage());
            logger.error("Rule execution failed", e);
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTimeMs(executionTime);
            result.addMetric("executionTimeMs", executionTime);
        }
        
        return result;
    }

    @Override
    public void removeRule(String ruleName) {
        logger.info("Removing rule: {}", ruleName);
        // Implementation would involve rebuilding KieContainer without the specified rule
        // For now, we'll log the operation
        logger.warn("Rule removal not fully implemented yet: {}", ruleName);
    }

    @Override
    public KieContainer getKieContainer() {
        return kieContainerRef.get();
    }

    private void reloadExistingRules(KieFileSystem kieFileSystem) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:rules/*.drl");
        
        for (Resource resource : resources) {
            logger.debug("Loading rule file: {}", resource.getFilename());
            kieFileSystem.write(ResourceFactory.newClassPathResource("rules/" + resource.getFilename()));
        }
    }
}