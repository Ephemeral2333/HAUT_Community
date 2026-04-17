package com.liyh.system.utils;

import com.liyh.common.result.Result;
import com.liyh.common.result.ResultCodeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Result Test")
class ResultTest {

    @Test
    @DisplayName("ok() should return code 200")
    void testOk_NoData() {
        var result = Result.ok();
        assertEquals(200, result.getCode());
        assertEquals("\u6210\u529f", result.getMessage()); // "成功"
    }

    @Test
    @DisplayName("ok(data) should wrap data")
    void testOk_WithData() {
        Result<String> result = Result.ok("hello");
        assertEquals(200, result.getCode());
        assertEquals("hello", result.getData());
    }

    @Test
    @DisplayName("fail() should return code 201")
    void testFail_NoData() {
        var result = Result.fail();
        assertEquals(201, result.getCode());
        assertEquals("\u5931\u8d25", result.getMessage()); // "失败"
    }

    @Test
    @DisplayName("build() with custom code and message")
    void testBuild_CustomCodeAndMessage() {
        Result<String> result = Result.build("data", 500, "error");
        assertEquals(500, result.getCode());
        assertEquals("error", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    @DisplayName("build() with ResultCodeEnum.PERMISSION should return 209")
    void testBuild_WithResultCodeEnum() {
        var result = Result.build(null, ResultCodeEnum.PERMISSION);
        assertEquals(209, result.getCode());
    }

    @Test
    @DisplayName("noUser() should return 219")
    void testNoUser() {
        var result = Result.noUser(null);
        assertEquals(219, result.getCode());
    }

    @Test
    @DisplayName("alreadyUserName() should return 220")
    void testAlreadyUserName() {
        var result = Result.alreadyUserName();
        assertEquals(220, result.getCode());
    }

    @Test
    @DisplayName("alreadyEmail() should return 221")
    void testAlreadyEmail() {
        var result = Result.alreadyEmail();
        assertEquals(221, result.getCode());
    }

    @Test
    @DisplayName("verifyError() should return 222")
    void testVerifyError() {
        var result = Result.verifyError();
        assertEquals(222, result.getCode());
    }

    @Test
    @DisplayName("message() chain should override message")
    void testMessageChain() {
        var result = Result.ok().message("custom");
        assertEquals("custom", result.getMessage());
    }

    @Test
    @DisplayName("code() chain should override code")
    void testCodeChain() {
        var result = Result.ok().code(666);
        assertEquals(666, result.getCode());
    }

    @Test
    @DisplayName("ok() with List data should work")
    void testOk_WithListData() {
        List<String> list = Arrays.asList("a", "b", "c");
        Result<List<String>> result = Result.ok(list);
        assertEquals(200, result.getCode());
        assertEquals(3, result.getData().size());
    }
}
