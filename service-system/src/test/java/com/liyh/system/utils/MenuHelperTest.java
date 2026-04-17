package com.liyh.system.utils;

import com.liyh.common.helper.MenuHelper;
import com.liyh.model.system.SysMenu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuHelper Tree Builder Test")
class MenuHelperTest {

    private SysMenu menu(Long id, Integer parentId, String name) {
        SysMenu m = new SysMenu();
        m.setId(id);
        m.setParentId(parentId);
        m.setName(name);
        return m;
    }

    @Test
    @DisplayName("empty list should return empty tree")
    void testBuildTree_Empty() {
        assertTrue(MenuHelper.buildTree(Collections.emptyList()).isEmpty());
    }

    @Test
    @DisplayName("single root node (parentId=0) should build tree with one root")
    void testBuildTree_SingleRoot() {
        List<SysMenu> result = MenuHelper.buildTree(
                Collections.singletonList(menu(1L, 0, "root")));
        assertEquals(1, result.size());
        assertEquals("root", result.get(0).getName());
        assertTrue(result.get(0).getChildren().isEmpty());
    }

    @Test
    @DisplayName("one root + two children should build 2-level tree")
    void testBuildTree_TwoLevels() {
        List<SysMenu> result = MenuHelper.buildTree(Arrays.asList(
                menu(1L, 0, "root"),
                menu(2L, 1, "child1"),
                menu(3L, 1, "child2")));
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getChildren().size());
    }

    @Test
    @DisplayName("3-level nesting should build correctly via recursion")
    void testBuildTree_ThreeLevels() {
        List<SysMenu> result = MenuHelper.buildTree(Arrays.asList(
                menu(1L, 0, "level1"),
                menu(2L, 1, "level2"),
                menu(3L, 2, "level3")));
        SysMenu level2 = result.get(0).getChildren().get(0);
        assertEquals("level2", level2.getName());
        assertEquals("level3", level2.getChildren().get(0).getName());
    }

    @Test
    @DisplayName("multiple root nodes should be identified separately")
    void testBuildTree_MultipleRoots() {
        List<SysMenu> result = MenuHelper.buildTree(Arrays.asList(
                menu(1L, 0, "root1"),
                menu(2L, 0, "root2"),
                menu(3L, 1, "child")));
        assertEquals(2, result.size());
        SysMenu root1 = result.stream().filter(m -> m.getId().equals(1L)).findFirst().orElseThrow();
        assertEquals(1, root1.getChildren().size());
    }

    @Test
    @DisplayName("unordered input (child before parent) should still build correctly")
    void testBuildTree_UnorderedInput() {
        List<SysMenu> result = MenuHelper.buildTree(Arrays.asList(
                menu(2L, 1, "child"),   // child first
                menu(1L, 0, "parent"))); // parent second
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getChildren().size());
    }
}
