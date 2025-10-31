package com.dmv.controller;

import com.dmv.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RulesManagementControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void testControllerExists() {
        RulesManagementController controller = new RulesManagementController(null);
        assertNotNull(controller);
    }

    @Test
    void testRuleContentSerialization() throws Exception {
        RuleContent ruleContent = new RuleContent();
        ruleContent.setName("TestRule");
        ruleContent.setContent("rule \"Test\" when then end");
        ruleContent.setDescription("A test rule");
        
        String json = objectMapper.writeValueAsString(ruleContent);
        assertNotNull(json);
        assertFalse(json.isEmpty());
        
        RuleContent deserialized = objectMapper.readValue(json, RuleContent.class);
        assertNotNull(deserialized);
        assertEquals("TestRule", deserialized.getName());
        assertEquals("rule \"Test\" when then end", deserialized.getContent());
    }

    @Test
    void testRuleValidation() {
        RuleContent validRule = new RuleContent();
        validRule.setName("ValidRule");
        validRule.setContent("rule \"Valid\" when $obj : Object() then end");
        
        assertNotNull(validRule);
        assertNotNull(validRule.getName());
        assertNotNull(validRule.getContent());
        assertFalse(validRule.getName().isEmpty());
        assertFalse(validRule.getContent().isEmpty());
    }

    @Test
    void testRulesListResponse() {
        RulesListResponse response = new RulesListResponse();
        
        List<RuleMetadata> rules = Arrays.asList(
            new RuleMetadata("Rule1", "First rule"),
            new RuleMetadata("Rule2", "Second rule")
        );
        
        response.setRules(rules);
        response.setTotalCount(2);
        
        assertNotNull(response);
        assertEquals(2, response.getTotalCount());
        assertNotNull(response.getRules());
        assertEquals(2, response.getRules().size());
        assertEquals("Rule1", response.getRules().get(0).getRuleName());
        assertEquals("Rule2", response.getRules().get(1).getRuleName());
    }
}