/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : haut_community

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 05/06/2023 16:52:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '上级部门id',
  `sort` int NULL DEFAULT 1 COMMENT '排序',
  `principal` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '负责人',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织机构' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '河南工业大学莲花街校区', 0, 1, '王倩倩', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept` VALUES (2, '人工智能与大数据学院', 1, 1, '王倩倩', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept` VALUES (3, '软件工程', 2, 2, '王刚', 1, '2023-05-26 22:57:52', '2023-05-26 22:57:52', 0);
INSERT INTO `sys_dept` VALUES (4, '人工智能', 2, 1, '王刚', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept` VALUES (5, '信息科学与工程学院', 1, 1, '王刚', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept` VALUES (6, '电子信息工程', 5, 1, '王倩倩', 1, '2023-05-26 22:57:31', '2023-05-26 22:57:31', 0);
INSERT INTO `sys_dept` VALUES (7, '计算机科学与技术', 5, 2, '王倩倩', 1, '2023-05-26 23:38:58', '2023-05-26 23:38:58', 0);
INSERT INTO `sys_dept` VALUES (8, '物联网工程', 5, 3, '王刚', 1, '2023-05-27 00:18:48', '2023-05-27 00:18:48', 1);
INSERT INTO `sys_dept` VALUES (9, '经济贸易学院', 1, 3, '王倩倩', 1, '2023-05-30 23:47:08', NULL, 0);

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '提示信息',
  `access_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO `sys_login_log` VALUES (1, 'admin', '0:0:0:0:0:0:0:1', 1, '登录成功', NULL, '2022-06-10 11:24:14', NULL, 0);
INSERT INTO `sys_login_log` VALUES (2, 'admin', '127.0.0.1', 1, '登录成功', NULL, '2022-06-10 11:53:43', NULL, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', 'admin', '系统管理员', '2021-05-31 18:09:18', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role` VALUES (2, '普通管理员', 'common', '普通管理员', '2021-06-01 08:38:40', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role` VALUES (3, '用户管理员', 'yhgly', '用户管理员', '2022-06-08 17:39:04', '2022-06-08 17:39:04', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员id',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `email` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` int NOT NULL DEFAULT 1 COMMENT '性别',
  `head_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1：正常 0：停用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', 'admin', 'admin@qq.com', 1, 'http://r61cnlsfq.hn-bkt.clouddn.com/7daa4595-dfde-45da-8513-c5c2b81d20cc', 1, 'admin', 1, '2021-05-31 18:08:43', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user` VALUES (2, '王倩倩', '96e79218965eb72c92a549dd5a330112', '王倩倩', 'wqq@qq.com', 0, 'http://r61cnlsfq.hn-bkt.clouddn.com/b09b3467-3d99-437a-bd2e-dd8c9be92bb8', 2, '111', 1, '2022-02-08 10:35:38', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user` VALUES (3, '王刚', '96e79218965eb72c92a549dd5a330112', '王刚', 'wanggang@qq.com', 1, NULL, 3, '222', 0, '2022-05-24 11:05:40', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user` VALUES (4, 'authTest', '96e79218965eb72c92a549dd5a330112', 'authTest', 'auth@qq.com', 1, NULL, 4, '333', 1, '2023-05-10 23:19:32', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user` VALUES (5, 'nice', '96e79218965eb72c92a549dd5a330112', '未设置昵称', 'nice@qq.com', 0, 'http://r61cnlsfq.hn-bkt.clouddn.com/b09b3467-3d99-437a-bd2e-dd8c9be92bb8', 5, 'nice', 1, '2023-06-04 16:08:58', '2023-06-05 16:40:02', 0);
INSERT INTO `sys_user` VALUES (13, '111', 'e10adc3949ba59abbe56e057f20f883e', '未设置昵称', '1612702983@qq.com', 0, NULL, 1, '1', 1, '2023-06-05 16:20:00', '2023-06-05 16:20:11', 1);
INSERT INTO `sys_user` VALUES (14, '1', 'e10adc3949ba59abbe56e057f20f883e', '未设置昵称', '1612702983@qq.com', 0, NULL, 1, '1', 1, '2023-06-05 16:23:56', '2023-06-05 16:24:09', 1);
INSERT INTO `sys_user` VALUES (15, '经贸小弟弟', 'e10adc3949ba59abbe56e057f20f883e', '未设置昵称', 'good@qq.com', 1, NULL, 9, '经贸小弟弟', 1, '2023-06-05 16:47:48', '2023-06-05 16:47:48', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` bigint NOT NULL DEFAULT 0 COMMENT '角色id',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2022-01-20 20:49:37', '2023-06-05 16:25:44', 1);
INSERT INTO `sys_user_role` VALUES (2, 1, 2, '2022-05-19 10:37:27', '2022-05-24 16:55:53', 1);
INSERT INTO `sys_user_role` VALUES (3, 1, 14, '2023-06-05 16:23:56', '2023-06-05 16:24:09', 1);
INSERT INTO `sys_user_role` VALUES (4, 2, 14, '2023-06-05 16:23:56', '2023-06-05 16:24:09', 1);
INSERT INTO `sys_user_role` VALUES (5, 1, 1, '2023-06-05 16:25:44', '2023-06-05 16:25:44', 0);
INSERT INTO `sys_user_role` VALUES (6, 2, 1, '2023-06-05 16:25:44', '2023-06-05 16:25:44', 0);
INSERT INTO `sys_user_role` VALUES (7, 3, 1, '2023-06-05 16:25:44', '2023-06-05 16:25:44', 0);
INSERT INTO `sys_user_role` VALUES (8, 1, 5, '2023-06-05 16:28:02', '2023-06-05 16:28:34', 1);
INSERT INTO `sys_user_role` VALUES (9, 1, 5, '2023-06-05 16:28:34', '2023-06-05 16:31:20', 1);

SET FOREIGN_KEY_CHECKS = 1;
