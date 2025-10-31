package com.dmv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DmvRulesEngineApplicationTests {

    @Test
    void testApplicationClassExists() {
        // Simple test to verify the main application class exists
        assertNotNull(DmvRulesEngineApplication.class);
        assertEquals("DmvRulesEngineApplication", DmvRulesEngineApplication.class.getSimpleName());
    }

    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // Verify the main method exists with correct signature
        var mainMethod = DmvRulesEngineApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }
}