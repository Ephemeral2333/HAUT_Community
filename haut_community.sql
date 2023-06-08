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

 Date: 05/06/2023 17:41:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for billboard
-- ----------------------------
DROP TABLE IF EXISTS `billboard`;
CREATE TABLE `billboard`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告',
  `user` bigint NOT NULL COMMENT '发布者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '公告时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '全站公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of billboard
-- ----------------------------
INSERT INTO `billboard` VALUES (1, 'R1.0 开始已实现护眼模式 ,妈妈再也不用担心我的眼睛了。', 1,'2020-11-19 17:16:19', '2020-11-19 17:16:19', 0);
INSERT INTO `billboard` VALUES (2, '系统已更新至最新版1.0.1', 1, '2020-11-19 17:16:19', '2020-11-19 17:16:19', 0);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `user_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作者ID',
  `topic_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'topic_id',
  `create_time` datetime NOT NULL COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint NOT NULL COMMENT '被关注人',
  `follower_id` bigint NOT NULL COMMENT '关注人',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (1, 1, 2, 0);
INSERT INTO `follow` VALUES (2, 1, 3, 0);
INSERT INTO `follow` VALUES (3, 1, 4, 0);

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'markdown内容',
  `user_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作者ID',
  `comments` int NOT NULL DEFAULT 0 COMMENT '评论统计',
  `collects` int NOT NULL DEFAULT 0 COMMENT '收藏统计',
  `view` int NOT NULL DEFAULT 0 COMMENT '浏览统计',
  `top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶，1-是，0-否',
  `essence` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否加精，1-是，0-否',
  `section_id` int NULL DEFAULT 0 COMMENT '专栏ID',
  `create_time` datetime NOT NULL COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `title`(`title`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '话题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, '1', '12\n2\n\n', '1349290158897311745', 0, 0, 77, b'0', b'0', 0, '2020-12-01 00:29:01', '2020-12-03 23:56:51', 0);
INSERT INTO `post` VALUES (2, '2021 健康，快乐', '2021的`FLAG`\n\n1. 技能进步\n2. 没有烦恼\n3. 发财 :smile:\n\n', '1349290158897311745', 0, 0, 21, b'0', b'0', 0, '2021-01-13 22:27:21', '2021-01-14 17:30:13', 0);
INSERT INTO `post` VALUES (3, 'hello，spring-security', ':hibiscus: spring-security\n\n', '1349290158897311745', 0, 0, 46, b'0', b'0', 0, '2020-12-03 20:56:51', NULL, 0);
INSERT INTO `post` VALUES (4, '哈哈哈，helloworld', '这是第一篇哦\n\n> hi :handshake: 你好\n\n`hello world`\n\n:+1: 很好\n', '1349290158897311745', 0, 0, 29, b'0', b'0', 1, '2020-11-28 19:40:02', '2020-11-28 19:46:39', 0);
INSERT INTO `post` VALUES (5, '哈哈哈，换了个dark主题', '主题更换为Dark\n\n', '1349290158897311745', 0, 0, 6, b'0', b'0', 0, '2020-11-30 23:27:00', NULL, 0);
INSERT INTO `post` VALUES (6, '嘿嘿，测试一下啊', '大家好\n`Hello everyone!`\n\n\n\n', '1349290158897311745', 0, 0, 7, b'0', b'0', 0, '2020-12-01 15:04:26', '2020-12-01 16:49:14', 0);
INSERT INTO `post` VALUES (7, '我要发财', '2021 冲冲冲！！！\n\n', '1349290158897311745', 0, 0, 94, b'0', b'0', 2, '2020-11-28 21:47:16', '2020-11-30 19:40:22', 0);
INSERT INTO `post` VALUES (9, '权限部分 OK', '1. 创建 ok\n2. 修改 ok\n3. 删除 ok\n\n', '1349290158897311745', 0, 0, 17, b'0', b'0', 0, '2021-01-14 16:16:49', '2021-01-14 16:18:53', 0);
INSERT INTO `post` VALUES (10, '测试', '测试\n\n', '1349290158897311745', 0, 0, 38, b'0', b'0', 0, '2020-12-01 15:35:34', NULL, 0);
INSERT INTO `post` VALUES (11, '聚合查询并统计', '* [x] SQL：\n\n```sql\nSELECT s.*,\nCOUNT(t.id) AS topics\nFROM section s\nLEFT JOIN topic t\nON s.id = t.section_id\nGROUP BY s.title\n```\n\n', '1349290158897311745', 0, 0, 55, b'0', b'0', 1, '2020-11-28 21:42:16', '2020-11-29 15:00:42', 0);
INSERT INTO `post` VALUES (12, '视频嵌入', ':+1:\n\n[https://www.bilibili.com/video/BV1w64y1f7w3](https://www.bilibili.com/video/BV1w64y1f7w3)\n\n[1](https://www.bilibili.com/video/BV1tp4y1x72w)\n\n```\n.vditor-reset pre > code\n```\n\n```\npublic class HelloWorld {\n\npublic static void main(String[] args) {\n    System.out.println(\"Hello World!\");\n}\n}\n```\n\n', '1349290158897311745', 0, 0, 41, b'0', b'0', 0, '2020-12-05 17:12:16', '2021-01-14 13:06:16', 0);

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE,
  INDEX `topic_id`(`topic_id`) USING BTREE,
  CONSTRAINT `post_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `post_tag_ibfk_2` FOREIGN KEY (`topic_id`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '话题-标签 中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_tag
-- ----------------------------

-- ----------------------------
-- Table structure for promotion
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '广告标题',
  `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '广告链接',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '说明',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告推广表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of promotion
-- ----------------------------
INSERT INTO `promotion` VALUES (1, '开发者头条', 'https://juejin.cn/', '开发者头条', 0);
INSERT INTO `promotion` VALUES (2, '并发编程网', 'https://juejin.cn/', '并发编程网', 0);
INSERT INTO `promotion` VALUES (3, '掘金', 'https://juejin.cn/', '掘金', 0);

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

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '标签',
  `topic_count` int NOT NULL DEFAULT 0 COMMENT '关联话题',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'java', 1, 0);
INSERT INTO `tag` VALUES (2, 'css', 1, 0);
INSERT INTO `tag` VALUES (3, 'mongodb', 1, 0);
INSERT INTO `tag` VALUES (4, 'python', 1, 0);
INSERT INTO `tag` VALUES (5, 'vue', 2, 0);

-- ----------------------------
-- Table structure for tip
-- ----------------------------
DROP TABLE IF EXISTS `tip`;
CREATE TABLE `tip`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `author` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '作者',
  `type` tinyint NOT NULL COMMENT '1：使用，0：过期',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24864 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '每日赠言' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tip
-- ----------------------------
INSERT INTO `tip` VALUES (1, '多锉出快锯，多做长知识。', '佚名', 1, 0);
INSERT INTO `tip` VALUES (2, '未来总留着什么给对它抱有信心的人。', '佚名', 1, 0);
INSERT INTO `tip` VALUES (3, '一个人的智慧不够用，两个人的智慧用不完。', '谚语', 1, 0);
INSERT INTO `tip` VALUES (4, '十个指头按不住十个跳蚤', '傣族', 1, 0);
INSERT INTO `tip` VALUES (5, '言不信者，行不果。', '墨子', 1, 0);
INSERT INTO `tip` VALUES (6, '攀援而登，箕踞而遨，则几数州之土壤，皆在衽席之下。', '柳宗元', 1, 0);
INSERT INTO `tip` VALUES (7, '美德大都包含在良好的习惯之内。', '帕利克', 1, 0);
INSERT INTO `tip` VALUES (8, '人有不及，可以情恕。', '《晋书》', 1, 0);
INSERT INTO `tip` VALUES (9, '明·吴惟顺', '法不传六耳', 1, 0);
INSERT INTO `tip` VALUES (10, '真正的朋友应该说真话，不管那话多么尖锐。', '奥斯特洛夫斯基', 1, 0);
INSERT INTO `tip` VALUES (11, '时间是一切财富中最宝贵的财富。', '德奥弗拉斯多', 1, 0);
INSERT INTO `tip` VALUES (12, '看人下菜碟', '民谚', 1, 0);
INSERT INTO `tip` VALUES (13, '如果不是怕别人反感，女人决不会保持完整的严肃。', '拉罗什福科', 1, 0);


SET FOREIGN_KEY_CHECKS = 1;
