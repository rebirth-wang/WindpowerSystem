package com.fastbee.framework.test.core.ut;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 简单测试 - 用于验证测试框架是否正常工作
 */
@DisplayName("简单测试")
public class SimpleTest {

    @Test
    @DisplayName("测试加法")
    public void testAddition() {
        assertEquals(4, 2 + 2);
    }

    @Test
    @DisplayName("测试字符串")
    public void testString() {
        String str = "Hello World";
        assertNotNull(str);
        assertTrue(str.contains("Hello"));
    }
}
