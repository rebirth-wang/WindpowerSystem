package com.fastbee.ai.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class AiSecuritySupportTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldFallbackToSystemWhenNoLoginUserExists() {
        SecurityContextHolder.clearContext();
        Assertions.assertEquals("system", AiSecuritySupport.resolveUsername());
    }
}
