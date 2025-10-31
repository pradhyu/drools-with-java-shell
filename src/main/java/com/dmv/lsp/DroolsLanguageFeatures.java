package com.dmv.lsp;

import org.eclipse.lsp4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced language features for Drools LSP support
 */
@Component
public class DroolsLanguageFeatures {

    private static final Logger logger = LoggerFactory.getLogger(DroolsLanguageFeatures.class);

    // Patterns for Drools syntax elements
    private static final Pattern RULE_PATTERN = Pattern.compile("rule\\s+\"([^\"]+)\"");
    private static final Pattern WHEN_PATTERN = Pattern.compile("when\\s*");
    private static final Pattern THEN_PATTERN = Pattern.compile("then\\s*");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$([a-zA-Z_][a-zA-Z0-9_]*)");
    private static final Pattern FACT_PATTERN = Pattern.compile("([A-Z][a-zA-Z0-9_]*)\\s*\\(");

    /**
     * Enhanced hover information with context-aware details
     */
    public Hover getEnhancedHover(String content, Position position) {
        String[] lines = content.split("\n");
        if (position.getLine() >= lines.length) {
            return null;
        }

        String line = lines[position.getLine()];
        int character = position.getCharacter();

        // Get word at position
        String wordAtPosition = getWordAtPosition(line, character);
        if (wordAtPosition == null) {
            return null;
        }

        String hoverText = getHoverTextForWord(wordAtPosition, line, content);
        if (hoverText != null) {
            MarkupContent markupContent = new MarkupContent();
            markupContent.setKind(MarkupKind.MARKDOWN);
            markupContent.setValue(hoverText);
            return new Hover(markupContent);
        }

        return null;
    }

    /**
     * Enhanced completion with context-aware suggestions
     */
    public List<CompletionItem> getEnhancedCompletions(String content, Position position) {
        List<CompletionItem> items = new ArrayList<>();
        
        String[] lines = content.split("\n");
        if (position.getLine() >= lines.length) {
            return items;
        }

        String currentLine = lines[position.getLine()];
        String linePrefix = currentLine.substring(0, Math.min(position.getCharacter(), currentLine.length()));

        // Context-aware completions
        if (isInRuleHeader(linePrefix)) {
            addRuleHeaderCompletions(items);
        } else if (isInWhenClause(content, position)) {
            addWhenClauseCompletions(items);
        } else if (isInThenClause(content, position)) {
            addThenClauseCompletions(items);
        } else {
            addGeneralCompletions(items);
        }

        // Add DMV-specific completions
        addDMVSpecificCompletions(items, linePrefix);

        return items;
    }

    /**
     * Enhanced diagnostics with detailed error analysis
     */
    public List<Diagnostic> getEnhancedDiagnostics(String content, List<String> errors, List<String> warnings) {
        List<Diagnostic> diagnostics = new ArrayList<>();

        // Add compilation errors with enhanced positioning
        for (String error : errors) {
            Diagnostic diagnostic = createDiagnosticFromError(content, error);
            diagnostics.add(diagnostic);
        }

        // Add compilation warnings
        for (String warning : warnings) {
            Diagnostic diagnostic = createDiagnosticFromWarning(content, warning);
            diagnostics.add(diagnostic);
        }

        // Add syntax analysis diagnostics
        diagnostics.addAll(performSyntaxAnalysis(content));

        return diagnostics;
    }

    private String getWordAtPosition(String line, int character) {
        if (character >= line.length()) {
            return null;
        }

        int start = character;
        int end = character;

        // Find word boundaries
        while (start > 0 && Character.isJavaIdentifierPart(line.charAt(start - 1))) {
            start--;
        }
        while (end < line.length() && Character.isJavaIdentifierPart(line.charAt(end))) {
            end++;
        }

        if (start == end) {
            return null;
        }

        return line.substring(start, end);
    }

    private String getHoverTextForWord(String word, String line, String content) {
        // Drools keywords
        switch (word.toLowerCase()) {
            case "rule":
                return "**Rule Declaration**\n\nDefines a business rule with a unique name.\n\n```drools\nrule \"Rule Name\"\n    when\n        // conditions\n    then\n        // actions\nend\n```";
            
            case "when":
                return "**When Clause**\n\nDefines the conditions (LHS - Left Hand Side) that must be satisfied for the rule to fire.\n\nConditions can include:\n- Fact patterns\n- Constraints\n- Logical operators (and, or, not)";
            
            case "then":
                return "**Then Clause**\n\nDefines the actions (RHS - Right Hand Side) to execute when rule conditions are met.\n\nActions can include:\n- Fact modifications\n- New fact insertions\n- Fact retractions\n- Method calls";
            
            case "end":
                return "**End Keyword**\n\nMarks the end of a rule definition.";
            
            case "import":
                return "**Import Statement**\n\nImports Java classes for use in rules.\n\n```drools\nimport com.dmv.model.LicenseRenewalRequest;\n```";
            
            case "package":
                return "**Package Declaration**\n\nDefines the package namespace for the rules file.\n\n```drools\npackage com.dmv.rules;\n```";
        }

        // DMV model classes
        switch (word) {
            case "LicenseRenewalRequest":
                return "**LicenseRenewalRequest**\n\nMain fact object representing a license renewal request.\n\n**Properties:**\n- applicantId: String\n- personalInfo: PersonalInfo\n- currentLicense: LicenseInfo\n- violations: List<Violation>\n- renewalType: RenewalType";
            
            case "RenewalDecision":
                return "**RenewalDecision**\n\nFact object representing the decision for a renewal request.\n\n**Properties:**\n- decision: DecisionType (APPROVED, REJECTED, REQUIRES_ACTION)\n- requirements: List<String>\n- reasons: List<String>\n- fee: BigDecimal\n- validUntil: LocalDate";
            
            case "PersonalInfo":
                return "**PersonalInfo**\n\nContains personal information about the applicant.\n\n**Properties:**\n- firstName, lastName: String\n- dateOfBirth: LocalDate\n- address: Address\n- phoneNumber: String\n\n**Methods:**\n- getAge(): int";
            
            case "LicenseInfo":
                return "**LicenseInfo**\n\nContains information about the current license.\n\n**Properties:**\n- licenseNumber: String\n- licenseClass: LicenseClass\n- issueDate, expirationDate: LocalDate\n- status: LicenseStatus\n\n**Methods:**\n- isExpired(): boolean\n- getMonthsSinceExpiration(): long";
        }

        // Check if it's a variable
        if (word.startsWith("$")) {
            return "**Rule Variable**\n\nVariable binding: `" + word + "`\n\nUsed to bind fact objects in rule conditions for reference in actions.";
        }

        return null;
    }

    private boolean isInRuleHeader(String linePrefix) {
        return linePrefix.trim().startsWith("rule");
    }

    private boolean isInWhenClause(String content, Position position) {
        // Simple heuristic: check if we're between "when" and "then"
        String beforePosition = getContentBeforePosition(content, position);
        int lastWhen = beforePosition.lastIndexOf("when");
        int lastThen = beforePosition.lastIndexOf("then");
        return lastWhen > lastThen;
    }

    private boolean isInThenClause(String content, Position position) {
        // Simple heuristic: check if we're between "then" and "end"
        String beforePosition = getContentBeforePosition(content, position);
        int lastThen = beforePosition.lastIndexOf("then");
        int lastEnd = beforePosition.lastIndexOf("end");
        return lastThen > lastEnd;
    }

    private String getContentBeforePosition(String content, Position position) {
        String[] lines = content.split("\n");
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < Math.min(position.getLine(), lines.length); i++) {
            sb.append(lines[i]).append("\n");
        }
        
        if (position.getLine() < lines.length) {
            String currentLine = lines[position.getLine()];
            sb.append(currentLine.substring(0, Math.min(position.getCharacter(), currentLine.length())));
        }
        
        return sb.toString();
    }

    private void addRuleHeaderCompletions(List<CompletionItem> items) {
        items.add(createSnippetCompletion("rule-template", "Rule Template", 
            "rule \"${1:RuleName}\"\n    when\n        ${2:conditions}\n    then\n        ${3:actions}\nend"));
    }

    private void addWhenClauseCompletions(List<CompletionItem> items) {
        // Fact patterns
        items.add(createCompletion("$request : LicenseRenewalRequest()", CompletionItemKind.Snippet));
        items.add(createCompletion("$decision : RenewalDecision()", CompletionItemKind.Snippet));
        
        // Common conditions
        items.add(createCompletion("personalInfo.age >= 18", CompletionItemKind.Snippet));
        items.add(createCompletion("currentLicense.expired == true", CompletionItemKind.Snippet));
        items.add(createCompletion("hasOutstandingViolations == true", CompletionItemKind.Snippet));
    }

    private void addThenClauseCompletions(List<CompletionItem> items) {
        // Common actions
        items.add(createCompletion("update($decision);", CompletionItemKind.Snippet));
        items.add(createCompletion("$decision.setDecision(DecisionType.APPROVED);", CompletionItemKind.Snippet));
        items.add(createCompletion("$decision.addRequirement(\"${1:requirement}\");", CompletionItemKind.Snippet));
        items.add(createCompletion("$decision.addReason(\"${1:reason}\");", CompletionItemKind.Snippet));
    }

    private void addGeneralCompletions(List<CompletionItem> items) {
        // Keywords
        items.add(createCompletion("rule", CompletionItemKind.Keyword));
        items.add(createCompletion("when", CompletionItemKind.Keyword));
        items.add(createCompletion("then", CompletionItemKind.Keyword));
        items.add(createCompletion("end", CompletionItemKind.Keyword));
        items.add(createCompletion("import", CompletionItemKind.Keyword));
        items.add(createCompletion("package", CompletionItemKind.Keyword));
    }

    private void addDMVSpecificCompletions(List<CompletionItem> items, String linePrefix) {
        if (linePrefix.contains("DecisionType.")) {
            items.add(createCompletion("APPROVED", CompletionItemKind.EnumMember));
            items.add(createCompletion("REJECTED", CompletionItemKind.EnumMember));
            items.add(createCompletion("REQUIRES_ACTION", CompletionItemKind.EnumMember));
        }
        
        if (linePrefix.contains("LicenseStatus.")) {
            items.add(createCompletion("ACTIVE", CompletionItemKind.EnumMember));
            items.add(createCompletion("EXPIRED", CompletionItemKind.EnumMember));
            items.add(createCompletion("SUSPENDED", CompletionItemKind.EnumMember));
            items.add(createCompletion("REVOKED", CompletionItemKind.EnumMember));
        }
    }

    private CompletionItem createCompletion(String label, CompletionItemKind kind) {
        CompletionItem item = new CompletionItem();
        item.setLabel(label);
        item.setKind(kind);
        item.setInsertText(label);
        return item;
    }

    private CompletionItem createSnippetCompletion(String label, String detail, String snippet) {
        CompletionItem item = new CompletionItem();
        item.setLabel(label);
        item.setDetail(detail);
        item.setKind(CompletionItemKind.Snippet);
        item.setInsertText(snippet);
        item.setInsertTextFormat(InsertTextFormat.Snippet);
        return item;
    }

    private Diagnostic createDiagnosticFromError(String content, String error) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(new Position(0, 0), new Position(0, 0)));
        diagnostic.setSeverity(DiagnosticSeverity.Error);
        diagnostic.setMessage(error);
        diagnostic.setSource("drools-compiler");
        return diagnostic;
    }

    private Diagnostic createDiagnosticFromWarning(String content, String warning) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(new Position(0, 0), new Position(0, 0)));
        diagnostic.setSeverity(DiagnosticSeverity.Warning);
        diagnostic.setMessage(warning);
        diagnostic.setSource("drools-compiler");
        return diagnostic;
    }

    private List<Diagnostic> performSyntaxAnalysis(String content) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Check for common syntax issues
            if (line.startsWith("rule") && !line.contains("\"")) {
                diagnostics.add(createSyntaxDiagnostic(i, "Rule name should be enclosed in quotes"));
            }
            
            if (line.equals("when") || line.equals("then")) {
                // Check if next non-empty line is properly indented
                for (int j = i + 1; j < lines.length; j++) {
                    String nextLine = lines[j].trim();
                    if (!nextLine.isEmpty()) {
                        if (!lines[j].startsWith("    ") && !nextLine.equals("end")) {
                            diagnostics.add(createSyntaxDiagnostic(j, "Content should be indented after " + line));
                        }
                        break;
                    }
                }
            }
        }

        return diagnostics;
    }

    private Diagnostic createSyntaxDiagnostic(int line, String message) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(new Position(line, 0), new Position(line, 0)));
        diagnostic.setSeverity(DiagnosticSeverity.Information);
        diagnostic.setMessage(message);
        diagnostic.setSource("drools-lsp");
        return diagnostic;
    }
}