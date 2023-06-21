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

 Date: 21/06/2023 08:35:06
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
  AUTO_INCREMENT = 6
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
INSERT INTO `billboard`
VALUES (5, '已经大致完成基本使用啦！', '2023-06-19 09:22:32', '2023-06-19 09:22:32', 0);

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint   NOT NULL COMMENT '用户ID',
    `topic_id`    bigint   NOT NULL COMMENT '话题ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collect
-- ----------------------------

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
    `parent_id`   bigint                                                   NOT NULL DEFAULT 0 COMMENT '父级评论',
    `create_time` datetime                                                 NOT NULL DEFAULT 'now()' COMMENT '发布时间',
    `update_time` datetime                                                 NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint                                                  NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '评论表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment`
VALUES (1, '写的好棒', 3, 1, 0, '2023-06-16 19:42:28', '2023-06-18 12:40:12', 0);
INSERT INTO `comment`
VALUES (2, '不错不错', 1, 1, 0, '2023-06-16 21:10:28', '2023-06-18 12:40:12', 0);
INSERT INTO `comment`
VALUES (4, '祝我们安好', 1, 2, 0, '2023-06-16 21:25:12', '2023-06-16 21:25:12', 0);
INSERT INTO `comment`
VALUES (5, '写的可以的', 1, 1, 2, '2023-06-16 21:10:47', '2023-06-18 14:32:22', 0);
INSERT INTO `comment`
VALUES (6, '继续加油', 1, 1, 2, '2023-06-18 14:32:00', '2023-06-18 14:34:32', 1);
INSERT INTO `comment`
VALUES (7, '继续加油', 1, 1, 0, '2023-06-18 14:35:38', '2023-06-18 14:35:38', 0);
INSERT INTO `comment`
VALUES (8, '测试成功', 1, 27, 0, '2023-06-19 17:16:58', '2023-06-19 17:16:58', 0);
INSERT INTO `comment`
VALUES (9, '你好啊，欢迎欢迎', 1, 6, 0, '2023-06-20 08:49:14', '2023-06-20 08:49:27', 1);
INSERT INTO `comment`
VALUES (10, '测试成功', 1, 32, 0, '2023-06-20 11:23:30', '2023-06-20 11:23:30', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`
(
    `id`       bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`  bigint NOT NULL COMMENT '用户id',
    `favor_id` bigint NOT NULL COMMENT '被点赞对象id',
    `type`     int    NULL DEFAULT NULL COMMENT '点赞类型（0:文章 1:评论）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite`
VALUES (4, 1, 2, 1);
INSERT INTO `favorite`
VALUES (8, 1, 6, 1);
INSERT INTO `favorite`
VALUES (9, 1, 1, 1);
INSERT INTO `favorite`
VALUES (10, 1, 8, 1);
INSERT INTO `favorite`
VALUES (16, 1, 2, 2);
INSERT INTO `favorite`
VALUES (17, 15, 2, 2);
INSERT INTO `favorite`
VALUES (18, 15, 4, 1);
INSERT INTO `favorite`
VALUES (19, 1, 32, 2);

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
  AUTO_INCREMENT = 19
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
VALUES (18, 2, 1, 0);
INSERT INTO `follow`
VALUES (19, 4, 1, 0);

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
    `forward`     bigint                                                  NOT NULL DEFAULT 0 COMMENT '转发量',
    `anonymous`   tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '匿名',
    `create_time` datetime                                                NOT NULL DEFAULT 'now()' COMMENT '发布时间',
    `update_time` datetime                                                NULL     DEFAULT 'now()' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint                                                 NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `user_id` (`user_id`) USING BTREE,
    INDEX `create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 32
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '话题表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post`
VALUES (1, 'hello啊大家好',
        ':eyes:️\n\n> 给大家看看我的新头像\n\n![53a3584b7f4460f2c6e229a2b5432a93.jpg](http://rw61twimb.hb-bkt.clouddn.com/community/fa98327347ad4012b9c2ec3e46a3a95c.jpg)\n\n\n\n',
        '2', 0, 0, 177, 0, b'0', b'0', 0, 0, '2020-12-01 00:29:01', '2023-06-21 08:30:32', 0);
INSERT INTO `post`
VALUES (2, '2023 健康，快乐', '2023的`FLAG`\n\n1. 技能进步\n2. 没有烦恼\n3. 发财 :smile: \n\n', '15', 0, 0, 67, 2, b'0',
        b'0', 4, 0, '2021-01-13 22:27:21', '2023-06-21 08:31:04', 0);
INSERT INTO `post`
VALUES (3, 'hello，spring-security', ':hibiscus: spring-security\n\n', '1', 0, 0, 55, 0, b'0', b'0', 0, 0,
        '2020-12-03 20:56:51', '2023-06-19 08:56:51', 0);
INSERT INTO `post`
VALUES (4, '哈哈哈，helloworld', '这是第一篇哦\n\n> hi :handshake: 你好\n\n`hello world`\n\n:+1: 很好\n', '3', 0, 0, 30,
        0, b'0', b'0', 0, 0, '2020-11-28 19:40:02', '2023-06-19 23:22:51', 0);
INSERT INTO `post`
VALUES (5, '哈哈哈，换了个dark主题', '主题更换为Dark\n\n', '1', 0, 0, 15, 0, b'0', b'0', 0, 0, '2020-11-30 23:27:00',
        '2023-06-20 11:23:51', 0);
INSERT INTO `post`
VALUES (6, '嘿嘿，测试一下啊', '大家好\n`Hello everyone!`\n\n\n\n', '4', 0, 0, 19, 0, b'0', b'0', 0, 0,
        '2020-12-01 15:04:26', '2023-06-20 11:25:35', 0);
INSERT INTO `post`
VALUES (7, '我要发财', '2021 冲冲冲！！！\n\n', '1', 0, 0, 100, 0, b'0', b'0', 0, 0, '2020-11-28 21:47:16',
        '2023-06-19 23:21:41', 0);
INSERT INTO `post`
VALUES (9, '权限部分 OK', '1. 创建 ok\n2. 修改 ok\n3. 删除 ok\n\n', '5', 0, 0, 27, 0, b'0', b'0', 0, 0,
        '2021-01-14 16:16:49', '2023-06-20 08:39:54', 0);
INSERT INTO `post`
VALUES (10, '测试', '测试\n\n', '1', 0, 0, 39, 0, b'0', b'0', 0, 0, '2020-12-01 15:35:34', '2023-06-18 17:11:55', 1);
INSERT INTO `post`
VALUES (11, '聚合查询并统计',
        '* [x] SQL：\n\n```sql\nSELECT s.*,\nCOUNT(t.id) AS topics\nFROM section s\nLEFT JOIN topic t\nON s.id = t.section_id\nGROUP BY s.title\n```\n\n',
        '1', 0, 0, 58, 0, b'0', b'0', 0, 0, '2020-11-28 21:42:16', '2023-06-19 23:21:36', 0);
INSERT INTO `post`
VALUES (12, '视频嵌入',
        ':+1:\n\n[https://www.bilibili.com/video/BV1w64y1f7w3](https://www.bilibili.com/video/BV1w64y1f7w3)\n\n[1](https://www.bilibili.com/video/BV1tp4y1x72w)\n\n```\n.vditor-reset pre > code\n```\n\n```\npublic class HelloWorld {\n\npublic static void main(String[] args) {\n    System.out.println(\"Hello World!\");\n}\n}\n```\n\n',
        '5', 0, 0, 55, 0, b'0', b'0', 0, 0, '2020-12-05 17:12:16', '2023-06-19 00:08:59', 0);
INSERT INTO `post`
VALUES (18, '111', '<pre><code >1111</code></pre><p><br></p>', '1', 0, 0, 0, 0, b'0', b'0', 0, 0, '2023-06-12 20:12:49',
        '2023-06-12 20:12:49', 1);
INSERT INTO `post`
VALUES (21, '权限部分 OK!', '<p>1. 创建 ok</p><p>2. 修改 ok</p><p>3. 删除 ok</p><p><br></p><p><br></p>', '1', 0, 0, 0,
        0, b'0', b'0', 0, 0, '2023-06-12 20:45:12', '2023-06-12 20:45:12', 1);
INSERT INTO `post`
VALUES (22, '权限部分 OK!',
        '<p>1. 创建 ok</p><p>2. 修改 ok</p><p>3. 删除 ok</p><p><br></p><div data-w-e-type=\"video\" data-w-e-is-void>\n<video poster=\"\" controls=\"true\" width=\"auto\" height=\"auto\"><source src=\"https://www.bilibili.com/video/BV1Am4y1v745\" /></video>\n</div><p><br></p>',
        '5', 0, 0, 0, 0, b'1', b'0', 0, 0, '2023-06-12 20:46:42', '2023-06-18 23:58:09', 1);
INSERT INTO `post`
VALUES (23, '现在是2023年6月15日20:36:37', '**课设好难啊，不知道写啥**\n\n---\n\n:confused: 哎，写的好像还行\n\n', '1', 0,
        0, 67, 0, b'0', b'0', 0, 0, '2023-06-15 20:37:49', '2023-06-21 08:30:35', 0);
INSERT INTO `post`
VALUES (25, '测试css', '##### 123\n\n---\n\n1. 首先，点个赞\n\n:+1:\n\n', '1', 0, 0, 26, 0, b'0', b'0', 0, 0,
        '2023-06-18 20:08:20', '2023-06-21 00:01:37', 0);
INSERT INTO `post`
VALUES (26, '测试vditor', '你好\n:smile:\n\n### hi\n\n* [ ] 123\n  qw\n\n> qw\n> qw`d`\n\n', '15', 0, 0, 6, 0, b'0',
        b'0', 0, 0, '2023-06-18 20:50:12', '2023-06-19 00:12:11', 1);
INSERT INTO `post`
VALUES (27, '测试文件上传',
        '![29aeb9db309305157567c237ceede3e0.jpeg](http://rw61twimb.hb-bkt.clouddn.com/community/86d7d8f536954338aff5cc3d08dbdf6b.jpeg)\n\n123\n[20230618224644812.mp4](http://rw61twimb.hb-bkt.clouddn.com/community/6744ad8ec1a74e92a6fd657d43ce58e9.mp4)\n[Java.md](http://rw61twimb.hb-bkt.clouddn.com/community/44de20ee0de644b78966d7bfa0c33663.md)\n\n',
        '1', 0, 0, 34, 0, b'0', b'0', 0, 0, '2023-06-18 22:40:59', '2023-06-21 08:30:14', 0);
INSERT INTO `post`
VALUES (28, '我也来发个帖子',
        '* [ ] 我的新头像\n\n:tada:️\n\n![53a3584b7f4460f2c6e229a2b5432a93.jpg](http://rw61twimb.hb-bkt.clouddn.com/community/87943f7df2954c7aa0e7e799e06d625c.jpg)\n\n',
        '15', 0, 0, 31, 0, b'0', b'0', 0, 0, '2023-06-19 00:03:29', '2023-06-20 11:23:12', 0);
INSERT INTO `post`
VALUES (29, '大家好，我是新来的',
        '冒个泡。。。\n\n![694ed4f96a14ca2299711140fdafc39b.jpg](http://rw61twimb.hb-bkt.clouddn.com/community/837e20e8ee47439792d2836104e69617.jpg)\n',
        '3', 0, 0, 62, 0, b'0', b'0', 0, 0, '2023-06-19 00:04:52', '2023-06-21 08:30:11', 0);
INSERT INTO `post`
VALUES (30, '测试一下标签啊', '这里是测试数据\n\n', '1', 0, 0, 3, 0, b'0', b'0', 0, 0, '2023-06-19 08:39:22',
        '2023-06-19 08:41:34', 1);
INSERT INTO `post`
VALUES (31, '测试', '测试一下删除标签\n', '1', 0, 0, 1, 0, b'0', b'0', 0, 0, '2023-06-19 08:47:07',
        '2023-06-19 08:47:21', 1);
INSERT INTO `post`
VALUES (32, '测试一下匿名功能', '测试测试\n\n', '1', 0, 0, 72, 1, b'0', b'0', 0, 1, '2023-06-20 09:35:18',
        '2023-06-21 08:30:08', 0);

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
  AUTO_INCREMENT = 110
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '话题-标签 中间表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
INSERT INTO `post_tag`
VALUES (99, 17, 27, 0);
INSERT INTO `post_tag`
VALUES (102, 19, 3, 0);
INSERT INTO `post_tag`
VALUES (106, 14, 1, 0);
INSERT INTO `post_tag`
VALUES (108, 7, 25, 0);
INSERT INTO `post_tag`
VALUES (109, 18, 23, 0);
INSERT INTO `post_tag`
VALUES (114, 7, 32, 0);
INSERT INTO `post_tag`
VALUES (115, 13, 2, 0);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT,
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '部门名称',
    `parent_id`   bigint                                                        NULL     DEFAULT 0 COMMENT '上级部门id',
    `sort`        int                                                           NULL     DEFAULT 1 COMMENT '排序',
    `principal`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '负责人',
    `status`      tinyint(1)                                                    NULL     DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_time` timestamp                                                     NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  tinyint                                                       NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织机构'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept`
VALUES (1, '河南工业大学莲花街校区', 0, 1, '王倩倩', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept`
VALUES (2, '人工智能与大数据学院', 1, 1, '王倩倩', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept`
VALUES (3, '软件工程', 2, 2, '王刚', 1, '2023-05-26 22:57:52', '2023-05-26 22:57:52', 0);
INSERT INTO `sys_dept`
VALUES (4, '人工智能', 2, 1, '王刚', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept`
VALUES (5, '信息科学与工程学院', 1, 1, '王刚', 1, '2023-05-26 22:22:32', '2023-05-26 22:22:32', 0);
INSERT INTO `sys_dept`
VALUES (6, '电子信息工程', 5, 1, '王倩倩', 1, '2023-05-26 22:57:31', '2023-05-26 22:57:31', 0);
INSERT INTO `sys_dept`
VALUES (7, '计算机科学与技术', 5, 2, '王倩倩', 1, '2023-05-26 23:38:58', '2023-05-26 23:38:58', 0);
INSERT INTO `sys_dept`
VALUES (8, '物联网工程', 5, 3, '王刚', 1, '2023-05-27 00:18:48', '2023-05-27 00:18:48', 1);
INSERT INTO `sys_dept`
VALUES (9, '经济贸易学院', 1, 3, '王倩倩', 1, '2023-05-30 23:47:08', NULL, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint                                                  NOT NULL AUTO_INCREMENT COMMENT '角色id',
    `name`        varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '角色名称',
    `code`        varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '角色编码',
    `remark`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '描述',
    `create_time` timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint                                                 NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '角色'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, '系统管理员', 'admin', '系统管理员', '2021-05-31 18:09:18', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role`
VALUES (2, '普通管理员', 'common', '普通管理员', '2021-06-01 08:38:40', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role`
VALUES (3, '用户管理员', 'manager', '用户管理员', '2022-06-08 17:39:04', '2023-06-19 17:28:09', 0);
INSERT INTO `sys_role`
VALUES (4, '审帖员', 'article', '审帖员，审核帖子是否违规', '2023-06-19 17:19:08', '2023-06-19 17:19:08', 0);
INSERT INTO `sys_role`
VALUES (5, '审核员', 'tip', '每日一句审核员', '2023-06-20 10:56:34', '2023-06-20 10:56:34', 0);

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
        'http://rw61twimb.hb-bkt.clouddn.com/community/a764f24707b84996b81b2285827f903c.jpeg', 1, 'admin', 1,
        '2021-05-31 18:08:43', '2023-06-20 08:42:36', 0);
INSERT INTO `sys_user`
VALUES (2, '王倩倩', '96e79218965eb72c92a549dd5a330112', '王倩倩', 'wqq@qq.com', 0,
        'http://rw61twimb.hb-bkt.clouddn.com/headphoto/29aeb9db309305157567c237ceede3e0.jpeg', 2, '111', 1,
        '2022-02-08 10:35:38', '2023-06-18 15:25:53', 0);
INSERT INTO `sys_user`
VALUES (3, '王刚', '96e79218965eb72c92a549dd5a330112', '王刚', 'wanggang@qq.com', 1,
        'http://rw61twimb.hb-bkt.clouddn.com/694ed4f96a14ca2299711140fdafc39b.jpg', 5, '222', 1, '2022-05-24 11:05:40',
        '2023-06-19 17:19:30', 0);
INSERT INTO `sys_user`
VALUES (4, 'authTest', '96e79218965eb72c92a549dd5a330112', 'authTest', 'auth@qq.com', 1,
        'http://rw61twimb.hb-bkt.clouddn.com/community/473b478ca2bf4bb49cafd1e4a13a94d5.jpeg', 9, '333', 1,
        '2023-05-10 23:19:32', '2023-06-19 17:25:16', 0);
INSERT INTO `sys_user`
VALUES (5, 'nice', '96e79218965eb72c92a549dd5a330112', 'nice', 'nice@qq.com', 0,
        'http://rw61twimb.hb-bkt.clouddn.com/community/0c1383239cbc4a6f80dde19e7d9cf2b0.jpeg', 9, 'nice', 1,
        '2023-06-04 16:08:58', '2023-06-20 10:56:53', 0);
INSERT INTO `sys_user`
VALUES (15, '小弟弟', '96e79218965eb72c92a549dd5a330112', '小弟弟', 'liyuanhaovip@163.com', 1,
        'http://rw61twimb.hb-bkt.clouddn.com/community/03fa9270e8934e579efc007e4eb9b6c7.jpg', 9, '经贸小弟弟', 1,
        '2023-06-05 16:47:48', '2023-06-20 10:50:21', 0);

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
  AUTO_INCREMENT = 14
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
VALUES (5, 1, 1, '2023-06-05 16:25:44', '2023-06-19 17:22:12', 1);
INSERT INTO `sys_user_role`
VALUES (6, 2, 1, '2023-06-05 16:25:44', '2023-06-19 17:22:12', 1);
INSERT INTO `sys_user_role`
VALUES (7, 3, 1, '2023-06-05 16:25:44', '2023-06-19 17:22:12', 1);
INSERT INTO `sys_user_role`
VALUES (8, 1, 5, '2023-06-05 16:28:02', '2023-06-05 16:28:34', 1);
INSERT INTO `sys_user_role`
VALUES (9, 1, 5, '2023-06-05 16:28:34', '2023-06-05 16:31:20', 1);
INSERT INTO `sys_user_role`
VALUES (10, 4, 3, '2023-06-19 17:19:30', '2023-06-19 17:19:30', 0);
INSERT INTO `sys_user_role`
VALUES (11, 1, 1, '2023-06-19 17:22:12', '2023-06-19 17:22:12', 0);
INSERT INTO `sys_user_role`
VALUES (12, 2, 2, '2023-06-19 17:25:10', '2023-06-19 17:25:10', 0);
INSERT INTO `sys_user_role`
VALUES (13, 3, 4, '2023-06-19 17:25:16', '2023-06-19 17:25:16', 0);
INSERT INTO `sys_user_role`
VALUES (14, 5, 5, '2023-06-20 10:56:53', '2023-06-20 10:56:53', 0);

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
  AUTO_INCREMENT = 20
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '标签表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag`
VALUES (7, '测试', 2, 0);
INSERT INTO `tag`
VALUES (8, '管理员', 0, 0);
INSERT INTO `tag`
VALUES (9, '课程设计', 0, 0);
INSERT INTO `tag`
VALUES (10, '爱情', 0, 0);
INSERT INTO `tag`
VALUES (12, '校园', 0, 0);
INSERT INTO `tag`
VALUES (13, '祝福', 1, 0);
INSERT INTO `tag`
VALUES (14, '新人报道', 1, 0);
INSERT INTO `tag`
VALUES (15, '小帅哥', 0, 0);
INSERT INTO `tag`
VALUES (16, '标签', 0, 0);
INSERT INTO `tag`
VALUES (17, '照片墙', 1, 0);
INSERT INTO `tag`
VALUES (18, '时间', 1, 0);
INSERT INTO `tag`
VALUES (19, '新鲜', 1, 0);

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
INSERT INTO `tip`
VALUES (24867, '欲买桂花同载酒，终不似，少年游。', '匿名', '刘过', 0, '2023-06-20 10:52:16', '2023-06-20 10:52:16');

-- ----------------------------
-- Table structure for tip_post
-- ----------------------------
DROP TABLE IF EXISTS `tip_post`;
CREATE TABLE `tip_post`
(
    `id`          bigint                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `author`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '' COMMENT '作者',
    `content`     varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '内容',
    `postman`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '' COMMENT '投稿人',
    `postman_id`  bigint                                                   NOT NULL DEFAULT 1 COMMENT '投稿人ID',
    `post_time`   datetime                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投稿时间',
    `is_accepted` tinyint                                                  NOT NULL DEFAULT 0 COMMENT '是否采纳（0:未处理 1:已采纳 2:已拒绝）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tip_post
-- ----------------------------
INSERT INTO `tip_post`
VALUES (1, '杜甫', '读书破万卷，下笔如有神', '匿名', 1, '2023-06-20 10:06:35', 0);
INSERT INTO `tip_post`
VALUES (3, '刘禹锡', '今日听君歌一曲,暂凭杯酒长精神', '匿名', 1, '2023-06-20 10:10:07', 0);
INSERT INTO `tip_post`
VALUES (4, '刘过', '欲买桂花同载酒，终不似，少年游。', '匿名', 15, '2023-06-20 10:51:36', 1);
INSERT INTO `tip_post`
VALUES (5, '111', '111', '111', 15, '2023-06-20 10:53:05', 2);

SET FOREIGN_KEY_CHECKS = 1;
