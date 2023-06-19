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

 Date: 18/06/2023 12:49:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for billboard
-- ----------------------------
DROP TABLE IF EXISTS `billboard`;
CREATE TABLE `billboard`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告',
    `create_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '公告时间',
    `update_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint                                                       NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '全站公告'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of billboard
-- ----------------------------
INSERT INTO `billboard`
VALUES (1, 'R1.0 开始已实现护眼模式 ,妈妈再也不用担心我的眼睛了。', '2020-11-19 17:16:19', '2020-11-19 17:16:19', 0);
INSERT INTO `billboard`
VALUES (2, '系统已更新至最新版1.0.6', '2020-11-19 17:16:19', '2023-06-08 09:59:45', 0);
INSERT INTO `billboard`
VALUES (4, '111', '2023-06-08 10:01:51', '2023-06-08 10:03:17', 1);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`          bigint                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content`     varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '内容',
    `user_id`     bigint                                                   NOT NULL COMMENT '作者ID',
    `topic_id`    bigint                                                   NOT NULL COMMENT 'topic_id',
    `favor`       bigint                                                   NOT NULL DEFAULT 0 COMMENT '点赞数',
    `parent_id`   bigint                                                   NOT NULL DEFAULT 0 COMMENT '父级评论',
    `create_time` datetime                                                 NOT NULL DEFAULT 'now()' COMMENT '发布时间',
    `update_time` datetime                                                 NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint                                                  NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '评论表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment`
VALUES (1, '写的好棒', 3, 1, 3, 0, '2023-06-16 19:42:28', '2023-06-18 12:40:12', 0);
INSERT INTO `comment`
VALUES (2, '不错不错', 1, 1, 2, 0, '2023-06-16 21:10:28', '2023-06-18 12:40:12', 0);
INSERT INTO `comment`
VALUES (4, '祝我们安好', 1, 2, 0, 0, '2023-06-16 21:25:12', '2023-06-16 21:25:12', 0);
INSERT INTO `comment`
VALUES (5, '写的可以的', 1, 1, 0, 2, '2023-06-16 21:10:47', '2023-06-18 10:36:23', 2);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`
(
    `id`       bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`  bigint NOT NULL COMMENT '用户id',
    `favor_id` bigint NOT NULL COMMENT '被点赞对象id',
    `type`     int    NULL DEFAULT NULL COMMENT '点赞类型（0:文章 1:评论）'
        PRIMARY KEY (`id`) USING BTREE,
    INDEX `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`
(
    `id`          bigint  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`   bigint  NOT NULL COMMENT '被关注人',
    `follower_id` bigint  NOT NULL COMMENT '关注人',
    `is_deleted`  tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow`
VALUES (2, 1, 3, 0);
INSERT INTO `follow`
VALUES (3, 1, 4, 0);
INSERT INTO `follow`
VALUES (10, 1, 2, 0);
INSERT INTO `follow`
VALUES (15, 2, 1, 0);

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`
(
    `id`          bigint                                                  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '标题',
    `content`     longtext CHARACTER SET utf8 COLLATE utf8_general_ci     NULL COMMENT 'markdown内容',
    `user_id`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '作者ID',
    `comments`    int                                                     NOT NULL DEFAULT 0 COMMENT '评论统计',
    `collects`    int                                                     NOT NULL DEFAULT 0 COMMENT '收藏统计',
    `view`        int                                                     NOT NULL DEFAULT 0 COMMENT '浏览统计',
    `favor`       bigint                                                  NOT NULL DEFAULT 0 COMMENT '点赞',
    `top`         bit(1)                                                  NOT NULL DEFAULT b'0' COMMENT '是否置顶，1-是，0-否',
    `essence`     bit(1)                                                  NOT NULL DEFAULT b'0' COMMENT '是否加精，1-是，0-否',
    `section_id`  int                                                     NULL     DEFAULT 0 COMMENT '专栏ID',
    `create_time` datetime                                                NOT NULL DEFAULT 'now()' COMMENT '发布时间',
    `update_time` datetime                                                NULL     DEFAULT 'now()' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint                                                 NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `user_id` (`user_id`) USING BTREE,
    INDEX `create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '话题表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post`
VALUES (1, '1', '12\n2\n\n', '2', 0, 1, 106, 0, b'0', b'0', 0, '2020-12-01 00:29:01', '2023-06-18 12:41:14', 0);
INSERT INTO `post`
VALUES (2, '2023 健康，快乐',
        '<p>2023的`FLAG`</p><p><br></p><p>1. 技能进步</p><p>2. 没有烦恼</p><p>3. 发财 :smile:</p><p><br></p><p><br></p>',
        '1', 0, 0, 24, 0, b'0', b'0', 0, '2021-01-13 22:27:21', '2023-06-16 21:24:49', 1);
INSERT INTO `post`
VALUES (3, 'hello，spring-security', ':hibiscus: spring-security\n\n', '1', 0, 0, 46, 0, b'0', b'0', 0,
        '2020-12-03 20:56:51', '2020-12-03 20:56:51', 0);
INSERT INTO `post`
VALUES (4, '哈哈哈，helloworld', '这是第一篇哦\n\n> hi :handshake: 你好\n\n`hello world`\n\n:+1: 很好\n', '1', 0, 0, 29,
        0, b'0', b'0', 1, '2020-11-28 19:40:02', '2023-06-15 19:25:18', 1);
INSERT INTO `post`
VALUES (5, '哈哈哈，换了个dark主题', '主题更换为Dark\n\n', '1', 0, 0, 6, 0, b'0', b'0', 0, '2020-11-30 23:27:00',
        '2020-11-30 23:27:00', 0);
INSERT INTO `post`
VALUES (6, '嘿嘿，测试一下啊', '大家好\n`Hello everyone!`\n\n\n\n', '1', 0, 0, 7, 0, b'0', b'0', 0,
        '2020-12-01 15:04:26', '2020-12-01 16:49:14', 0);
INSERT INTO `post`
VALUES (7, '我要发财', '2021 冲冲冲！！！\n\n', '1', 0, 0, 94, 0, b'0', b'0', 2, '2020-11-28 21:47:16',
        '2020-11-30 19:40:22', 0);
INSERT INTO `post`
VALUES (9, '权限部分 OK', '1. 创建 ok\n2. 修改 ok\n3. 删除 ok\n\n', '1', 0, 0, 17, 0, b'0', b'0', 0,
        '2021-01-14 16:16:49', '2021-01-14 16:18:53', 1);
INSERT INTO `post`
VALUES (10, '测试', '测试\n\n', '1', 0, 0, 38, 0, b'0', b'0', 0, '2020-12-01 15:35:34', '2020-12-01 15:35:34', 0);
INSERT INTO `post`
VALUES (11, '聚合查询并统计',
        '* [x] SQL：\n\n```sql\nSELECT s.*,\nCOUNT(t.id) AS topics\nFROM section s\nLEFT JOIN topic t\nON s.id = t.section_id\nGROUP BY s.title\n```\n\n',
        '1', 0, 0, 55, 0, b'0', b'0', 1, '2020-11-28 21:42:16', '2020-11-29 15:00:42', 0);
INSERT INTO `post`
VALUES (12, '视频嵌入',
        ':+1:\n\n[https://www.bilibili.com/video/BV1w64y1f7w3](https://www.bilibili.com/video/BV1w64y1f7w3)\n\n[1](https://www.bilibili.com/video/BV1tp4y1x72w)\n\n```\n.vditor-reset pre > code\n```\n\n```\npublic class HelloWorld {\n\npublic static void main(String[] args) {\n    System.out.println(\"Hello World!\");\n}\n}\n```\n\n',
        '1', 0, 0, 41, 0, b'0', b'0', 0, '2020-12-05 17:12:16', '2021-01-14 13:06:16', 0);
INSERT INTO `post`
VALUES (18, '111', '<pre><code >1111</code></pre><p><br></p>', '1', 0, 0, 0, 0, b'0', b'0', 0, '2023-06-12 20:12:49',
        '2023-06-12 20:12:49', 1);
INSERT INTO `post`
VALUES (21, '权限部分 OK!', '<p>1. 创建 ok</p><p>2. 修改 ok</p><p>3. 删除 ok</p><p><br></p><p><br></p>', '1', 0, 0, 0,
        0, b'0', b'0', 0, '2023-06-12 20:45:12', '2023-06-12 20:45:12', 1);
INSERT INTO `post`
VALUES (22, '权限部分 OK!',
        '<p>1. 创建 ok</p><p>2. 修改 ok</p><p>3. 删除 ok</p><p><br></p><div data-w-e-type=\"video\" data-w-e-is-void>\n<video poster=\"\" controls=\"true\" width=\"auto\" height=\"auto\"><source src=\"https://www.bilibili.com/video/BV1Am4y1v745\" /></video>\n</div><p><br></p>',
        '2', 0, 0, 0, 0, b'1', b'0', 0, '2023-06-12 20:46:42', '2023-06-15 19:52:07', 1);
INSERT INTO `post`
VALUES (23, '现在是2023年6月15日20:36:37',
        '<p>我在课设课上写课设，不知道该写什么课设</p><p>:sob::sob::cry:</p><p><a href=\"http://www.baidu.com\" target=\"_blank\">怎么办呢</a></p>',
        '1', 0, 0, 4, 0, b'0', b'0', 0, '2023-06-15 20:37:49', '2023-06-16 21:23:10', 0);
INSERT INTO `post`
VALUES (24, '我爱黎铭杰', '<p>我是lhy</p>', '1', 0, 0, 0, 0, b'0', b'0', 0, '2023-06-16 18:46:25',
        '2023-06-16 18:49:35', 1);

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`
(
    `id`         bigint  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tag_id`     bigint  NOT NULL COMMENT '标签ID',
    `topic_id`   bigint  NOT NULL COMMENT '话题ID',
    `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `tag_id` (`tag_id`) USING BTREE,
    INDEX `topic_id` (`topic_id`) USING BTREE,
    CONSTRAINT `post_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `post_tag_ibfk_2` FOREIGN KEY (`topic_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 79
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '话题-标签 中间表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
INSERT INTO `post_tag`
VALUES (52, 1, 1, 0);
INSERT INTO `post_tag`
VALUES (53, 2, 9, 0);
INSERT INTO `post_tag`
VALUES (60, 7, 18, 0);
INSERT INTO `post_tag`
VALUES (65, 2, 21, 0);
INSERT INTO `post_tag`
VALUES (73, 2, 22, 0);
INSERT INTO `post_tag`
VALUES (74, 9, 23, 0);
INSERT INTO `post_tag`
VALUES (75, 10, 24, 0);
INSERT INTO `post_tag`
VALUES (76, 11, 24, 0);
INSERT INTO `post_tag`
VALUES (77, 12, 24, 0);
INSERT INTO `post_tag`
VALUES (78, 13, 2, 0);

-- ----------------------------
-- Table structure for promotion
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '广告标题',
    `link`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '广告链接',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '说明',
    `is_deleted`  tinyint                                                       NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告推广表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of promotion
-- ----------------------------
INSERT INTO `promotion`
VALUES (1, '开发者头条', 'https://juejin.cn/', '开发者头条', 0);
INSERT INTO `promotion`
VALUES (2, '并发编程网', 'https://juejin.cn/', '并发编程网', 0);
INSERT INTO `promotion`
VALUES (3, '掘金', 'https://juejin.cn/', '掘金', 0);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `id`        bigint                                                       NOT NULL AUTO_INCREMENT,
    `name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '部门名称',
    `parent_id` bigint                                                       NULL     DEFAULT 0 COMMENT '上级部门id',
    `sort`      int                                                          NULL     DEFAULT 1 COMMENT '排序',
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
INSERT INTO `sys_role`
VALUES (1, '系统管理员', 'admin', '系统管理员', '2021-05-31 18:09:18', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role`
VALUES (2, '普通管理员', 'common', '普通管理员', '2021-06-01 08:38:40', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role`
VALUES (3, '用户管理员', 'yhgly', '用户管理员', '2022-06-08 17:39:04', '2022-06-08 17:39:04', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '会员id',
    `username`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '用户名',
    `password`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '密码',
    `nickname`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '姓名',
    `email`       varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '邮箱',
    `sex`         int                                                           NOT NULL DEFAULT 1 COMMENT '性别',
    `head_url`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '头像地址',
    `dept_id`     bigint                                                        NULL     DEFAULT NULL COMMENT '部门id',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '描述',
    `status`      tinyint                                                       NULL     DEFAULT 1 COMMENT '状态（1：正常 0：停用）',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint                                                       NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 18
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', 'admin', 'admin@qq.com', 1,
        'http://r61cnlsfq.hn-bkt.clouddn.com/7daa4595-dfde-45da-8513-c5c2b81d20cc', 1, 'admin', 1,
        '2021-05-31 18:08:43', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user`
VALUES (2, '王倩倩', '96e79218965eb72c92a549dd5a330112', '王倩倩', 'wqq@qq.com', 0,
        'http://r61cnlsfq.hn-bkt.clouddn.com/b09b3467-3d99-437a-bd2e-dd8c9be92bb8', 2, '111', 1, '2022-02-08 10:35:38',
        '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user`
VALUES (3, '王刚', '96e79218965eb72c92a549dd5a330112', '王刚', 'wanggang@qq.com', 1, NULL, 3, '222', 0,
        '2022-05-24 11:05:40', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user`
VALUES (4, 'authTest', '96e79218965eb72c92a549dd5a330112', 'authTest', 'auth@qq.com', 1, NULL, 4, '333', 1,
        '2023-05-10 23:19:32', '2023-05-30 22:43:33', 0);
INSERT INTO `sys_user`
VALUES (5, 'nice', '96e79218965eb72c92a549dd5a330112', '未设置昵称', 'nice@qq.com', 0,
        'http://r61cnlsfq.hn-bkt.clouddn.com/b09b3467-3d99-437a-bd2e-dd8c9be92bb8', 5, 'nice', 1, '2023-06-04 16:08:58',
        '2023-06-05 16:40:02', 0);
INSERT INTO `sys_user`
VALUES (13, '111', 'e10adc3949ba59abbe56e057f20f883e', '未设置昵称', '1612702983@qq.com', 0, NULL, 1, '1', 1,
        '2023-06-05 16:20:00', '2023-06-05 16:20:11', 1);
INSERT INTO `sys_user`
VALUES (14, '1', 'e10adc3949ba59abbe56e057f20f883e', '未设置昵称', '1612702983@qq.com', 0, NULL, 1, '1', 1,
        '2023-06-05 16:23:56', '2023-06-05 16:24:09', 1);
INSERT INTO `sys_user`
VALUES (15, '经贸小弟弟', 'e10adc3949ba59abbe56e057f20f883e', '未设置昵称', 'good@qq.com', 1, NULL, 9, '经贸小弟弟', 1,
        '2023-06-05 16:47:48', '2023-06-05 16:47:48', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `role_id`     bigint    NOT NULL DEFAULT 0 COMMENT '角色id',
    `user_id`     bigint    NOT NULL DEFAULT 0 COMMENT '用户id',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint   NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `role_id` (`role_id`) USING BTREE,
    INDEX `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户角色'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role`
VALUES (1, 1, 1, '2022-01-20 20:49:37', '2023-06-05 16:25:44', 1);
INSERT INTO `sys_user_role`
VALUES (2, 1, 2, '2022-05-19 10:37:27', '2022-05-24 16:55:53', 1);
INSERT INTO `sys_user_role`
VALUES (3, 1, 14, '2023-06-05 16:23:56', '2023-06-05 16:24:09', 1);
INSERT INTO `sys_user_role`
VALUES (4, 2, 14, '2023-06-05 16:23:56', '2023-06-05 16:24:09', 1);
INSERT INTO `sys_user_role`
VALUES (5, 1, 1, '2023-06-05 16:25:44', '2023-06-05 16:25:44', 0);
INSERT INTO `sys_user_role`
VALUES (6, 2, 1, '2023-06-05 16:25:44', '2023-06-05 16:25:44', 0);
INSERT INTO `sys_user_role`
VALUES (7, 3, 1, '2023-06-05 16:25:44', '2023-06-05 16:25:44', 0);
INSERT INTO `sys_user_role`
VALUES (8, 1, 5, '2023-06-05 16:28:02', '2023-06-05 16:28:34', 1);
INSERT INTO `sys_user_role`
VALUES (9, 1, 5, '2023-06-05 16:28:34', '2023-06-05 16:31:20', 1);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `id`          bigint                                                  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `name`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '标签',
    `topic_count` int                                                     NOT NULL DEFAULT 0 COMMENT '关联话题',
    `is_deleted`  tinyint                                                 NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `name` (`name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 14
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '标签表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag`
VALUES (1, 'java', 1, 0);
INSERT INTO `tag`
VALUES (2, 'css', 10, 0);
INSERT INTO `tag`
VALUES (3, 'mongodb', 0, 0);
INSERT INTO `tag`
VALUES (4, 'python', 0, 0);
INSERT INTO `tag`
VALUES (5, 'vue', 0, 0);
INSERT INTO `tag`
VALUES (7, '测试', 1, 0);
INSERT INTO `tag`
VALUES (8, '管理员', 0, 0);
INSERT INTO `tag`
VALUES (9, '课程设计', 0, 0);
INSERT INTO `tag`
VALUES (10, '爱情', 0, 0);
INSERT INTO `tag`
VALUES (11, '男女', 0, 0);
INSERT INTO `tag`
VALUES (12, '校园', 0, 0);
INSERT INTO `tag`
VALUES (13, '祝福', 0, 0);

-- ----------------------------
-- Table structure for tip
-- ----------------------------
DROP TABLE IF EXISTS `tip`;
CREATE TABLE `tip`
(
    `id`          bigint UNSIGNED                                          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content`     varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '内容',
    `user`        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '投稿人',
    `author`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT '' COMMENT '作者',
    `is_deleted`  tinyint                                                  NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    `create_time` datetime                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 24865
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '每日赠言'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tip
-- ----------------------------
INSERT INTO `tip`
VALUES (1, '多锉出快锯，多做长知识。', '小红', '佚名', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (2, '未来总留着什么给对它抱有信心的人。', '小红', '佚名', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (3, '一个人的智慧不够用，两个人的智慧用不完。', '小红', '谚语', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (4, '十个指头按不住十个跳蚤', '小红', '傣族', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (5, '言不信者，行不果。', '小红', '墨子', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (6, '攀援而登，箕踞而遨，则几数州之土壤，皆在衽席之下。', '小红', '柳宗元', 0, '2023-06-05 21:59:23',
        '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (7, '美德大都包含在良好的习惯之内。', '小红', '帕利克', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (8, '人有不及，可以情恕。', '小红', '《晋书》', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (9, '明·吴惟顺', '小红', '法不传六耳', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (10, '真正的朋友应该说真话，不管那话多么尖锐。', '小红', '奥斯特洛夫斯基', 0, '2023-06-05 21:59:23',
        '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (11, '时间是一切财富中最宝贵的财富', '小红', '德奥弗拉斯多', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (12, '看人下菜碟', '小红', '民谚', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip`
VALUES (13, '如果不是怕别人反感，女人决不会保持完整的严肃。', '小红', '拉罗什福科', 1, '2023-06-05 21:59:23',
        '2023-06-06 11:14:04');
INSERT INTO `tip`
VALUES (24864, '读书百遍,其义自见', 'admin', '朱熹', 0, '2023-06-06 15:52:47', '2023-06-06 15:53:02');

SET FOREIGN_KEY_CHECKS = 1;
