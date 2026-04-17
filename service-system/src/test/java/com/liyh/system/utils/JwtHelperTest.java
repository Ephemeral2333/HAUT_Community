package com.liyh.system.utils;

import com.liyh.common.utils.JwtHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtHelper Test")
class JwtHelperTest {

    private static final String TEST_USER_ID  = "1001";
    private static final String TEST_USERNAME = "testUser";

    // ==================== createToken ====================

    @Test
    @DisplayName("createToken should not return null or blank")
    void testCreateToken_ShouldNotBeNullOrEmpty() {
        String token = JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("createToken should produce 3-part JWT format")
    void testCreateToken_ShouldBeJwtFormat() {
        String token = JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME);
        assertEquals(3, token.split("\\.").length);
    }

    // ==================== getUserId ====================

    @Test
    @DisplayName("getUserId should match original userId")
    void testGetUserId_ShouldMatchOriginal() {
        String token = JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME);
        assertEquals(TEST_USER_ID, JwtHelper.getUserId(token));
    }

    @Test
    @DisplayName("getUserId with null token should return null")
    void testGetUserId_NullToken() {
        assertNull(JwtHelper.getUserId(null));
    }

    @Test
    @DisplayName("getUserId with blank token should return null")
    void testGetUserId_BlankToken() {
        assertNull(JwtHelper.getUserId(""));
        assertNull(JwtHelper.getUserId("   "));
    }

    @Test
    @DisplayName("getUserId with tampered token should return null (security)")
    void testGetUserId_TamperedToken() {
        String[] parts = JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME).split("\\.");
        assertNull(JwtHelper.getUserId(parts[0] + ".TAMPERED." + parts[2]));
    }

    // ==================== getUsername ====================

    @Test
    @DisplayName("getUsername should match original username")
    void testGetUsername_ShouldMatchOriginal() {
        String token = JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME);
        assertEquals(TEST_USERNAME, JwtHelper.getUsername(token));
    }

    @Test
    @DisplayName("getUsername with null token should return empty string")
    void testGetUsername_NullToken() {
        assertEquals("", JwtHelper.getUsername(null));
    }

    // ==================== isTokenValid ====================

    @Test
    @DisplayName("fresh token should be valid")
    void testIsTokenValid_FreshToken() {
        assertTrue(JwtHelper.isTokenValid(JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME)));
    }

    @Test
    @DisplayName("null/blank token should be invalid")
    void testIsTokenValid_NullOrBlank() {
        assertFalse(JwtHelper.isTokenValid(null));
        assertFalse(JwtHelper.isTokenValid(""));
        assertFalse(JwtHelper.isTokenValid("   "));
    }

    @Test
    @DisplayName("random string should be invalid")
    void testIsTokenValid_RandomString() {
        assertFalse(JwtHelper.isTokenValid("not.a.jwt"));
    }

    @Test
    @DisplayName("tampered token should be invalid (security)")
    void testIsTokenValid_TamperedToken() {
        String[] parts = JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME).split("\\.");
        assertFalse(JwtHelper.isTokenValid(parts[0] + ".TAMPERED." + parts[2]));
    }

    // ==================== getExpirationDate ====================

    @Test
    @DisplayName("expiration date should match yyyy/MM/dd HH:mm:ss format")
    void testGetExpirationDate_Format() {
        String exp = JwtHelper.getExpirationDate(JwtHelper.createToken(TEST_USER_ID, TEST_USERNAME));
        assertTrue(exp.matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    // ==================== end-to-end ====================

    @Test
    @DisplayName("full login flow: create token then parse back user info")
    void testFullLoginFlow() {
        String token = JwtHelper.createToken("88888", "admin");
        assertTrue(JwtHelper.isTokenValid(token));
        assertEquals("88888", JwtHelper.getUserId(token));
        assertEquals("admin", JwtHelper.getUsername(token));
    }
}
