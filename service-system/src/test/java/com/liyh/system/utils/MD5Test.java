package com.liyh.system.utils;

import com.liyh.common.utils.MD5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MD5 工具类单元测试
 * 覆盖点：加密正确性、幂等性、特殊字符、空字符串边界
 */
@DisplayName("MD5加密工具测试")
class MD5Test {

    @Test
    @DisplayName("加密结果不应为null")
    void testEncrypt_ShouldNotReturnNull() {
        String result = MD5.encrypt("hello");
        assertNotNull(result);
    }

    @Test
    @DisplayName("加密结果应为32位小写十六进制")
    void testEncrypt_ShouldReturn32LowerHex() {
        String result = MD5.encrypt("hello");
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"),
                "MD5结果应该是32位小写十六进制，实际: " + result);
    }

    @Test
    @DisplayName("已知字符串加密结果应与预期MD5值一致")
    void testEncrypt_KnownValue() {
        // echo -n "111111" | md5sum => 96e79218965eb72c92a549dd5a330112
        String result = MD5.encrypt("111111");
        assertEquals("96e79218965eb72c92a549dd5a330112", result);
    }

    @Test
    @DisplayName("相同输入多次加密结果应保持一致（幂等性）")
    void testEncrypt_Idempotent() {
        String input = "testPassword@2024";
        String first  = MD5.encrypt(input);
        String second = MD5.encrypt(input);
        assertEquals(first, second, "同一字符串多次MD5加密结果应相同");
    }

    @Test
    @DisplayName("不同输入的加密结果应不同")
    void testEncrypt_DifferentInputDifferentOutput() {
        String result1 = MD5.encrypt("password123");
        String result2 = MD5.encrypt("password456");
        assertNotEquals(result1, result2, "不同密码的MD5值不应相同");
    }

    @Test
    @DisplayName("特殊字符和中文输入不应抛出异常")
    void testEncrypt_SpecialCharacters() {
        assertDoesNotThrow(() -> MD5.encrypt("!@#$%^&*()中文测试"));
        assertDoesNotThrow(() -> MD5.encrypt("   spaces   "));
        assertDoesNotThrow(() -> MD5.encrypt("\n\t\r"));
    }

    @Test
    @DisplayName("空字符串加密应正常返回固定MD5值")
    void testEncrypt_EmptyString() {
        // echo -n "" | md5sum => d41d8cd98f00b204e9800998ecf8427e
        String result = MD5.encrypt("");
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
    }

    @Test
    @DisplayName("密码加密后验证登录场景：重新加密应与存储值一致")
    void testEncrypt_LoginScenario() {
        String rawPassword = "111111";
        String storedHash  = MD5.encrypt(rawPassword);   // 注册时存库
        String loginHash   = MD5.encrypt(rawPassword);   // 登录时重新加密
        assertEquals(storedHash, loginHash, "登录验证：重新加密的密码应与数据库存储值一致");
    }
}
