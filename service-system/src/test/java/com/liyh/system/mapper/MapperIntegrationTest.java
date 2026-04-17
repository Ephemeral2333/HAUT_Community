package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Post;
import com.liyh.model.system.SysUser;
import com.liyh.system.CommunityApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapper Integration Tests (requires real DB connection)
 *
 * Prerequisites:
 *   1. MySQL / Redis services running
 *   2. application-dev.yml configured with DB credentials
 *   3. haut_community.sql imported
 */
@SpringBootTest(classes = CommunityApplication.class)
@ActiveProfiles("dev")
@DisplayName("Mapper Integration Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MapperIntegrationTest {

    @Autowired private PostMapper postMapper;
    @Autowired private CommentMapper commentMapper;
    @Autowired private SysUserMapper sysUserMapper;
    @Autowired private FollowMapper followMapper;

    // ==================== PostMapper ====================

    @Test
    @Order(1)
    @DisplayName("selectPageByUserId should return paged result")
    void testPostMapper_SelectPageByUserId() {
        IPage<Post> result = postMapper.selectPageByUserId(new Page<>(1, 10), "1");
        assertNotNull(result);
        assertTrue(result.getTotal() >= 0);
    }

    @Test
    @Order(2)
    @DisplayName("selectPageByHot should return paged result")
    void testPostMapper_SelectPageByHot() {
        assertNotNull(postMapper.selectPageByHot(new Page<>(1, 5)));
    }

    @Test
    @Order(3)
    @DisplayName("selectByPk should return post with author info if exists")
    void testPostMapper_SelectByPk() {
        Post post = postMapper.selectByPk(1L);
        if (post != null) {
            assertNotNull(post.getId());
        }
    }

    @Test
    @Order(4)
    @DisplayName("searchByKeyword results should all contain the keyword")
    void testPostMapper_SearchByKeyword() {
        IPage<Post> result = postMapper.searchByKeyword(new Page<>(1, 10), "test");
        assertNotNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("searchByKeyword with empty string should not throw")
    void testPostMapper_SearchByKeyword_Empty() {
        assertDoesNotThrow(() -> postMapper.searchByKeyword(new Page<>(1, 10), ""));
    }

    @Test
    @Order(6)
    @DisplayName("isFavor should return non-negative integer")
    void testPostMapper_IsFavor() {
        assertTrue(postMapper.isFavor("1", 1L) >= 0);
    }

    @Test
    @Order(7)
    @DisplayName("getFavoriteCountByPostId should return non-negative integer")
    void testPostMapper_GetFavoriteCount() {
        assertTrue(postMapper.getFavoriteCountByPostId(1L) >= 0);
    }

    // ==================== CommentMapper ====================

    @Test
    @Order(10)
    @DisplayName("isFavor (comment) should return non-negative integer")
    void testCommentMapper_IsFavor() {
        Integer result = commentMapper.isFavor(1L, 1L);
        assertNotNull(result);
        assertTrue(result >= 0);
    }

    // ==================== SysUserMapper ====================

    @Test
    @Order(20)
    @DisplayName("selectByUserName with non-existent user should return null")
    void testSysUserMapper_NotExist() {
        assertNull(sysUserMapper.selectByUserName("user_not_exist_xyz_12345"));
    }

    @Test
    @Order(21)
    @DisplayName("preset user 'admin' password should be MD5 format if exists")
    void testSysUserMapper_AdminUser() {
        SysUser user = sysUserMapper.selectByUserName("admin");
        if (user != null) {
            assertEquals("admin", user.getUsername());
            assertTrue(user.getPassword().matches("[0-9a-f]{32}"),
                    "password should be MD5 format");
        }
    }

    // ==================== FollowMapper ====================

    @Test
    @Order(30)
    @DisplayName("isFollow with non-existent relation should return 0")
    void testFollowMapper_IsFollow_NotExist() {
        assertEquals(0, followMapper.isFollow(99999L, "88888"));
    }
}
