/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : haut_community

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 06/03/2026 17:55:25
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
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '公告时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '全站公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of billboard
-- ----------------------------
INSERT INTO `billboard` VALUES (1, 'R1.0 开始已实现护眼模式 ,妈妈再也不用担心我的眼睛了。', '2020-11-19 17:16:19', '2020-11-19 17:16:19', 0);
INSERT INTO `billboard` VALUES (2, '系统已更新至最新版1.0.6', '2020-11-19 17:16:19', '2023-06-08 09:59:45', 0);
INSERT INTO `billboard` VALUES (4, '111', '2023-06-08 10:01:51', '2023-06-08 10:03:17', 1);
INSERT INTO `billboard` VALUES (5, '已经大致完成基本使用啦！', '2023-06-19 09:22:32', '2023-06-19 09:22:32', 0);
INSERT INTO `billboard` VALUES (6, '我又回来了！', '2025-09-08 21:09:40', '2025-09-08 21:09:40', 0);

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of collect
-- ----------------------------
INSERT INTO `collect` VALUES (4, 1, 29, '2023-06-21 09:55:23');
INSERT INTO `collect` VALUES (5, 1, 32, '2023-06-21 17:37:46');
INSERT INTO `collect` VALUES (6, 18, 27, '2023-06-22 13:48:28');
INSERT INTO `collect` VALUES (7, 1, 33, '2023-06-23 22:45:55');
INSERT INTO `collect` VALUES (9, 20, 23, '2026-02-01 17:32:40');
INSERT INTO `collect` VALUES (11, 20, 25, '2026-02-01 17:59:01');
INSERT INTO `collect` VALUES (12, 19, 54, '2026-03-04 21:59:58');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `topic_id` bigint NOT NULL COMMENT 'topic_id',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级评论',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, '写的好棒', 3, 1, 0, '2023-06-16 19:42:28', '2023-06-18 12:40:12', 0);
INSERT INTO `comment` VALUES (2, '不错不错', 1, 1, 0, '2023-06-16 21:10:28', '2023-06-18 12:40:12', 0);
INSERT INTO `comment` VALUES (4, '祝我们安好', 1, 2, 0, '2023-06-16 21:25:12', '2023-06-16 21:25:12', 0);
INSERT INTO `comment` VALUES (5, '写的可以的', 1, 1, 2, '2023-06-16 21:10:47', '2023-06-18 14:32:22', 0);
INSERT INTO `comment` VALUES (6, '继续加油', 1, 1, 2, '2023-06-18 14:32:00', '2023-06-18 14:34:32', 1);
INSERT INTO `comment` VALUES (7, '继续加油', 1, 1, 0, '2023-06-18 14:35:38', '2023-06-18 14:35:38', 0);
INSERT INTO `comment` VALUES (8, '测试成功', 1, 27, 0, '2023-06-19 17:16:58', '2023-06-19 17:16:58', 0);
INSERT INTO `comment` VALUES (9, '你好啊，欢迎欢迎', 1, 6, 0, '2023-06-20 08:49:14', '2023-06-20 08:49:27', 1);
INSERT INTO `comment` VALUES (10, '测试成功', 1, 32, 0, '2023-06-20 11:23:30', '2023-06-20 11:23:30', 0);
INSERT INTO `comment` VALUES (11, '欢迎欢迎', 1, 33, 0, '2023-06-23 20:04:55', '2023-06-23 20:05:38', 1);
INSERT INTO `comment` VALUES (12, '', 1, 27, 0, '2023-06-24 19:58:52', '2023-06-24 19:58:56', 1);
INSERT INTO `comment` VALUES (13, 'yeah', 1, 27, 8, '2023-06-24 20:00:13', '2023-06-24 20:00:13', 0);
INSERT INTO `comment` VALUES (14, '加油', 1, 33, 0, '2025-09-08 21:18:31', '2025-09-08 21:18:31', 0);
INSERT INTO `comment` VALUES (15, '嘿嘿', 1, 33, 14, '2025-09-08 21:18:38', '2025-09-08 21:18:42', 1);
INSERT INTO `comment` VALUES (16, '继续加油', 19, 35, 0, '2026-01-31 20:29:16', '2026-01-31 20:29:16', 0);
INSERT INTO `comment` VALUES (17, '继续加油哦', 20, 25, 0, '2026-02-01 17:59:23', '2026-02-01 17:59:23', 0);
INSERT INTO `comment` VALUES (18, '好久不见', 1, 23, 0, '2026-02-01 18:02:32', '2026-02-01 18:02:32', 0);
INSERT INTO `comment` VALUES (19, '不错啊', 20, 11, 0, '2026-02-01 18:13:24', '2026-02-01 18:13:24', 0);
INSERT INTO `comment` VALUES (20, '顶一下', 19, 57, 0, '2026-03-04 20:17:23', '2026-03-04 20:17:23', 0);
INSERT INTO `comment` VALUES (21, '谢谢噜', 19, 54, 0, '2026-03-04 21:37:39', '2026-03-04 21:37:39', 0);
INSERT INTO `comment` VALUES (22, '赞同赞同', 1, 39, 0, '2026-03-06 14:26:29', '2026-03-06 14:26:29', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `favor_id` bigint NOT NULL COMMENT '被点赞对象id',
  `type` int NULL DEFAULT NULL COMMENT '点赞类型（1:评论 2:文章）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (4, 1, 2, 1);
INSERT INTO `favorite` VALUES (8, 1, 6, 1);
INSERT INTO `favorite` VALUES (9, 1, 1, 1);
INSERT INTO `favorite` VALUES (10, 1, 8, 1);
INSERT INTO `favorite` VALUES (16, 1, 2, 2);
INSERT INTO `favorite` VALUES (17, 15, 2, 2);
INSERT INTO `favorite` VALUES (18, 15, 4, 1);
INSERT INTO `favorite` VALUES (20, 1, 7, 2);
INSERT INTO `favorite` VALUES (22, 1, 29, 2);
INSERT INTO `favorite` VALUES (23, 18, 33, 2);
INSERT INTO `favorite` VALUES (24, 18, 2, 2);
INSERT INTO `favorite` VALUES (26, 1, 11, 1);
INSERT INTO `favorite` VALUES (27, 1, 33, 2);
INSERT INTO `favorite` VALUES (28, 1, 13, 1);
INSERT INTO `favorite` VALUES (29, 1, 14, 1);
INSERT INTO `favorite` VALUES (32, 19, 16, 1);
INSERT INTO `favorite` VALUES (36, 20, 27, 2);
INSERT INTO `favorite` VALUES (38, 1, 36, 2);
INSERT INTO `favorite` VALUES (39, 20, 25, 2);
INSERT INTO `favorite` VALUES (41, 20, 18, 1);
INSERT INTO `favorite` VALUES (42, 20, 23, 2);
INSERT INTO `favorite` VALUES (43, 20, 11, 2);
INSERT INTO `favorite` VALUES (44, 20, 38, 2);
INSERT INTO `favorite` VALUES (45, 19, 39, 2);
INSERT INTO `favorite` VALUES (46, 19, 57, 2);
INSERT INTO `favorite` VALUES (47, 1, 54, 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (2, 1, 3, 0);
INSERT INTO `follow` VALUES (3, 1, 4, 0);
INSERT INTO `follow` VALUES (10, 1, 2, 0);
INSERT INTO `follow` VALUES (18, 2, 1, 0);
INSERT INTO `follow` VALUES (19, 4, 1, 0);
INSERT INTO `follow` VALUES (20, 3, 1, 0);
INSERT INTO `follow` VALUES (22, 1, 20, 0);
INSERT INTO `follow` VALUES (23, 15, 19, 0);
INSERT INTO `follow` VALUES (24, 1, 19, 0);
INSERT INTO `follow` VALUES (25, 18, 3, 0);

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `from_user_id` bigint NULL DEFAULT NULL COMMENT '发送者ID (系统通知为空)',
  `to_user_id` bigint NOT NULL COMMENT '接收者ID',
  `type` tinyint NOT NULL COMMENT '通知类型 (1:点赞帖子 2:点赞评论 3:评论帖子 4:回复评论 5:关注 6:系统)',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID (帖子ID/评论ID/用户ID)',
  `target_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标标题摘要',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读 (0:未读 1:已读)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记 (0:可用 1:已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_to_user_id`(`to_user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '站内通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 20, 1, 1, 27, '测试文件上传', 'test003 赞了你的帖子', 1, '2026-02-01 17:52:07', '2026-02-01 17:53:30', 0);
INSERT INTO `notification` VALUES (2, 20, 1, 1, 23, '现在是2023年6月15日20:36:37', 'test003 赞了你的帖子', 1, '2026-02-01 17:53:51', '2026-02-01 17:53:58', 0);
INSERT INTO `notification` VALUES (3, 1, 20, 1, 36, '大家好，RabbitMQ功能已经实现', 'admin 赞了你的帖子', 1, '2026-02-01 17:56:32', '2026-02-01 17:56:37', 0);
INSERT INTO `notification` VALUES (4, 20, 1, 1, 25, '测试css', 'test003 赞了你的帖子', 1, '2026-02-01 17:58:14', '2026-02-01 17:58:25', 0);
INSERT INTO `notification` VALUES (5, 20, 1, 3, 25, '测试css', 'test003 评论了你的帖子：继续加油哦', 1, '2026-02-01 17:59:23', '2026-02-01 18:00:20', 0);
INSERT INTO `notification` VALUES (6, 20, 1, 1, 23, '现在是2023年6月15日20:36:37', 'test003 赞了你的帖子', 1, '2026-02-01 18:10:27', '2026-02-01 18:10:34', 0);
INSERT INTO `notification` VALUES (7, 20, 1, 1, 11, '聚合查询并统计', 'test003 赞了你的帖子', 1, '2026-02-01 18:13:19', '2026-02-01 18:13:34', 0);
INSERT INTO `notification` VALUES (8, 20, 1, 3, 11, '聚合查询并统计', 'test003 评论了你的帖子：不错啊', 1, '2026-02-01 18:13:24', '2026-02-01 18:13:36', 0);
INSERT INTO `notification` VALUES (9, 1, 1, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 1, '2026-02-01 18:45:33', '2026-02-01 18:48:48', 0);
INSERT INTO `notification` VALUES (10, 1, 2, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 0, '2026-02-01 18:45:33', '2026-02-01 18:45:33', 0);
INSERT INTO `notification` VALUES (11, 1, 3, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 1, '2026-02-01 18:45:33', '2026-03-04 21:47:11', 0);
INSERT INTO `notification` VALUES (12, 1, 4, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 0, '2026-02-01 18:45:33', '2026-02-01 18:45:33', 0);
INSERT INTO `notification` VALUES (13, 1, 5, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 0, '2026-02-01 18:45:33', '2026-02-01 18:45:33', 0);
INSERT INTO `notification` VALUES (14, 1, 15, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 0, '2026-02-01 18:45:33', '2026-02-01 18:45:33', 0);
INSERT INTO `notification` VALUES (15, 1, 18, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 1, '2026-02-01 18:45:33', '2026-03-04 21:25:20', 0);
INSERT INTO `notification` VALUES (16, 1, 19, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 1, '2026-02-01 18:45:33', '2026-03-02 14:32:11', 0);
INSERT INTO `notification` VALUES (17, 1, 20, 6, NULL, '系统维护通知', '近期将进行系统维护，请做好准备', 1, '2026-02-01 18:45:33', '2026-02-01 18:45:42', 0);
INSERT INTO `notification` VALUES (18, 1, 1, 6, NULL, '活动通知', '最近可能有好活动哦', 1, '2026-02-01 18:46:01', '2026-02-01 18:48:46', 0);
INSERT INTO `notification` VALUES (19, 1, 2, 6, NULL, '活动通知', '最近可能有好活动哦', 0, '2026-02-01 18:46:01', '2026-02-01 18:46:01', 0);
INSERT INTO `notification` VALUES (20, 1, 3, 6, NULL, '活动通知', '最近可能有好活动哦', 1, '2026-02-01 18:46:01', '2026-03-04 21:47:11', 0);
INSERT INTO `notification` VALUES (21, 1, 4, 6, NULL, '活动通知', '最近可能有好活动哦', 0, '2026-02-01 18:46:01', '2026-02-01 18:46:01', 0);
INSERT INTO `notification` VALUES (22, 1, 5, 6, NULL, '活动通知', '最近可能有好活动哦', 0, '2026-02-01 18:46:01', '2026-02-01 18:46:01', 0);
INSERT INTO `notification` VALUES (23, 1, 15, 6, NULL, '活动通知', '最近可能有好活动哦', 0, '2026-02-01 18:46:01', '2026-02-01 18:46:01', 0);
INSERT INTO `notification` VALUES (24, 1, 18, 6, NULL, '活动通知', '最近可能有好活动哦', 1, '2026-02-01 18:46:01', '2026-03-04 21:25:20', 0);
INSERT INTO `notification` VALUES (25, 1, 19, 6, NULL, '活动通知', '最近可能有好活动哦', 1, '2026-02-01 18:46:01', '2026-03-02 14:32:09', 0);
INSERT INTO `notification` VALUES (26, 1, 20, 6, NULL, '活动通知', '最近可能有好活动哦', 1, '2026-02-01 18:46:01', '2026-02-01 18:46:12', 0);
INSERT INTO `notification` VALUES (27, 20, 1, 1, 38, '测试延迟发信插件', 'test003 赞了你的帖子', 1, '2026-02-01 19:22:50', '2026-02-01 19:23:04', 0);
INSERT INTO `notification` VALUES (28, 19, 15, 5, NULL, NULL, 'woshisg 关注了你', 0, '2026-03-02 16:40:16', '2026-03-02 16:40:16', 0);
INSERT INTO `notification` VALUES (29, 19, 1, 1, 57, '寻物启事：在图书馆二楼遗失一串钥匙，急！', 'woshisg 赞了你的帖子', 1, '2026-03-04 20:17:18', '2026-03-06 10:30:26', 0);
INSERT INTO `notification` VALUES (30, 19, 1, 3, 57, '寻物启事：在图书馆二楼遗失一串钥匙，急！', 'woshisg 评论了你的帖子：顶一下', 1, '2026-03-04 20:17:23', '2026-03-06 10:30:26', 0);
INSERT INTO `notification` VALUES (31, 19, 1, 3, 54, '关于校园网频繁断连和网速慢的终极解决方法', 'woshisg 评论了你的帖子：谢谢噜', 1, '2026-03-04 21:37:39', '2026-03-06 10:30:26', 0);
INSERT INTO `notification` VALUES (32, 19, 1, 5, NULL, NULL, 'woshisg 关注了你', 1, '2026-03-04 21:43:06', '2026-03-06 10:30:26', 0);
INSERT INTO `notification` VALUES (33, 3, 18, 5, NULL, NULL, '王刚 关注了你', 0, '2026-03-04 21:47:04', '2026-03-04 21:47:04', 0);
INSERT INTO `notification` VALUES (34, 1, 19, 3, 39, '极力推荐！！！工大附近美食', 'admin 评论了你的帖子：赞同赞同', 0, '2026-03-06 14:26:29', '2026-03-06 14:26:29', 0);

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT 'markdown内容',
  `user_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '作者ID',
  `view` int NOT NULL DEFAULT 0 COMMENT '浏览统计',
  `top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶，1-是，0-否',
  `essence` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否加精，1-是，0-否',
  `forward` bigint NOT NULL DEFAULT 0 COMMENT '转发量',
  `anonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '匿名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0=待发布，1=已发布',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '话题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 'hello啊大家好', ':eyes:️\n\n> 给大家看看我的新头像\n\n![53a3584b7f4460f2c6e229a2b5432a93.jpg](http://rw61twimb.hb-bkt.clouddn.com/community/fa98327347ad4012b9c2ec3e46a3a95c.jpg)\n\n\n\n', '2', 182, b'0', b'0', 2, 0, '2020-12-01 00:29:01', '2026-02-01 19:17:37', 0, 1);
INSERT INTO `post` VALUES (2, '2023 健康，快乐', '2023的`FLAG`\n\n1. 技能进步\n2. 没有烦恼\n3. 发财 :smile: \n\n', '15', 80, b'0', b'0', 4, 0, '2021-01-13 22:27:21', '2026-03-02 16:42:15', 0, 1);
INSERT INTO `post` VALUES (3, 'hello，spring-security', ':hibiscus: spring-security\n\n', '1', 57, b'0', b'0', 0, 0, '2020-12-03 20:56:51', '2026-01-31 21:34:16', 0, 1);
INSERT INTO `post` VALUES (4, '哈哈哈，helloworld', '这是第一篇哦\n\n> hi :handshake: 你好\n\n`hello world`\n\n:+1: 很好\n', '3', 31, b'0', b'0', 0, 0, '2020-11-28 19:40:02', '2026-02-01 18:53:42', 0, 1);
INSERT INTO `post` VALUES (5, '哈哈哈，换了个dark主题', '主题更换为Dark\n\n', '1', 17, b'0', b'0', 0, 0, '2020-11-30 23:27:00', '2023-06-23 20:08:10', 0, 1);
INSERT INTO `post` VALUES (6, '嘿嘿，测试一下啊', '大家好\n`Hello everyone!`\n\n\n\n', '4', 20, b'0', b'0', 0, 0, '2020-12-01 15:04:26', '2026-03-06 14:29:00', 0, 1);
INSERT INTO `post` VALUES (7, '我要发财', '2023 冲冲冲！！！\n\n', '1', 112, b'0', b'0', 0, 0, '2020-11-28 21:47:16', '2026-03-06 11:02:49', 0, 1);
INSERT INTO `post` VALUES (9, '权限部分 OK', '1. 创建 ok\n2. 修改 ok\n3. 删除 ok\n\n', '5', 29, b'0', b'0', 0, 0, '2021-01-14 16:16:49', '2026-03-06 14:29:00', 0, 1);
INSERT INTO `post` VALUES (10, '测试', '测试\n\n', '1', 39, b'0', b'0', 0, 0, '2020-12-01 15:35:34', '2023-06-18 17:11:55', 1, 1);
INSERT INTO `post` VALUES (11, '聚合查询并统计', '* [x] SQL：\n\n```sql\nSELECT s.*,\nCOUNT(t.id) AS topics\nFROM section s\nLEFT JOIN topic t\nON s.id = t.section_id\nGROUP BY s.title\n```\n\n', '1', 64, b'0', b'0', 0, 0, '2020-11-28 21:42:16', '2026-02-01 19:27:37', 0, 1);
INSERT INTO `post` VALUES (12, '视频嵌入', ':+1:\n\n[https://www.bilibili.com/video/BV1w64y1f7w3](https://www.bilibili.com/video/BV1w64y1f7w3)\n\n[1](https://www.bilibili.com/video/BV1tp4y1x72w)\n\n```\n.vditor-reset pre > code\n```\n\n```\npublic class HelloWorld {\n\npublic static void main(String[] args) {\n    System.out.println(\"Hello World!\");\n}\n}\n```\n\n', '5', 56, b'0', b'0', 0, 0, '2020-12-05 17:12:16', '2023-06-21 19:57:46', 0, 1);
INSERT INTO `post` VALUES (18, '111', '<pre><code >1111</code></pre><p><br></p>', '1', 0, b'0', b'0', 0, 0, '2023-06-12 20:12:49', '2023-06-12 20:12:49', 1, 1);
INSERT INTO `post` VALUES (21, '权限部分 OK!', '<p>1. 创建 ok</p><p>2. 修改 ok</p><p>3. 删除 ok</p><p><br></p><p><br></p>', '1', 0, b'0', b'0', 0, 0, '2023-06-12 20:45:12', '2023-06-12 20:45:12', 1, 1);
INSERT INTO `post` VALUES (22, '权限部分 OK!', '<p>1. 创建 ok</p><p>2. 修改 ok</p><p>3. 删除 ok</p><p><br></p><div data-w-e-type=\"video\" data-w-e-is-void>\n<video poster=\"\" controls=\"true\" width=\"auto\" height=\"auto\"><source src=\"https://www.bilibili.com/video/BV1Am4y1v745\" /></video>\n</div><p><br></p>', '5', 0, b'1', b'0', 0, 0, '2023-06-12 20:46:42', '2023-06-18 23:58:09', 1, 1);
INSERT INTO `post` VALUES (23, '现在是2023年6月15日20:36:37', '**课设好难啊，不知道写啥**\n\n---\n\n:confused: 哎，写的好像还行\n\n', '1', 78, b'0', b'0', 0, 0, '2023-06-15 20:37:49', '2026-02-01 18:53:42', 0, 1);
INSERT INTO `post` VALUES (25, '测试css', '##### 123\n\n---\n\n1. 首先，点个赞\n\n:+1:\n\n', '1', 38, b'0', b'0', 0, 0, '2023-06-18 20:08:20', '2026-02-01 18:53:42', 0, 1);
INSERT INTO `post` VALUES (26, '测试vditor', '你好\n:smile:\n\n### hi\n\n* [ ] 123\n  qw\n\n> qw\n> qw`d`\n\n', '15', 6, b'0', b'0', 0, 0, '2023-06-18 20:50:12', '2023-06-19 00:12:11', 1, 1);
INSERT INTO `post` VALUES (27, '测试文件上传', '![febd8fda2ce2f7bc7278d589e0229b05.jpeg](http://tbdmep631.hb-bkt.clouddn.com/community/d215d0fbb5974621ac088db24d58010b.jpeg)\n\n', '1', 72, b'1', b'0', 0, 0, '2023-06-18 22:40:59', '2026-03-06 17:32:04', 0, 1);
INSERT INTO `post` VALUES (28, '我也来发个帖子', '* [ ] 我的新头像\n\n:tada:️\n\n![53a3584b7f4460f2c6e229a2b5432a93.jpg](http://rw61twimb.hb-bkt.clouddn.com/community/87943f7df2954c7aa0e7e799e06d625c.jpg)\n\n', '15', 33, b'0', b'0', 0, 0, '2023-06-19 00:03:29', '2026-01-31 19:54:04', 0, 1);
INSERT INTO `post` VALUES (29, '大家好，我是新来的', '冒个泡。。。\n\n![694ed4f96a14ca2299711140fdafc39b.jpg](http://rw61twimb.hb-bkt.clouddn.com/community/837e20e8ee47439792d2836104e69617.jpg)\n', '3', 76, b'0', b'0', 0, 0, '2023-06-19 00:04:52', '2026-03-06 11:02:49', 0, 1);
INSERT INTO `post` VALUES (30, '测试一下标签啊', '这里是测试数据\n\n', '1', 3, b'0', b'0', 0, 0, '2023-06-19 08:39:22', '2023-06-19 08:41:34', 1, 1);
INSERT INTO `post` VALUES (31, '测试', '测试一下删除标签\n', '1', 1, b'0', b'0', 0, 0, '2023-06-19 08:47:07', '2023-06-19 08:47:21', 1, 1);
INSERT INTO `post` VALUES (32, '测试一下匿名功能', '测试测试\n\n', '1', 85, b'0', b'1', 0, 1, '2023-06-20 09:35:18', '2026-03-06 14:29:00', 0, 1);
INSERT INTO `post` VALUES (33, '大家吼，我是新来的', ':tada:️ :tada:️ :tada:️ **热烈庆祝社区开业！！！**\n![image.png](http://t9q95cvdk.hb-bkt.clouddn.com/community/f2f27b7736544b9cb17fa3e141378553.png)\n\n', '18', 67, b'1', b'1', 5, 0, '2023-06-21 20:42:50', '2026-03-06 10:52:49', 0, 1);
INSERT INTO `post` VALUES (34, '测试发帖', '测试发帖\n', '1', 3, b'0', b'1', 0, 1, '2023-06-24 19:44:30', '2025-10-20 20:06:05', 0, 1);
INSERT INTO `post` VALUES (35, '重新归来的帖子', '我回来了\n', '19', 0, b'0', b'0', 1, 0, '2026-01-31 20:28:03', '2026-01-31 20:29:10', 0, 1);
INSERT INTO `post` VALUES (36, '大家好，RabbitMQ功能已经实现', '**大家好，RabbitMQ功能已经实现**\n', '20', 9, b'0', b'0', 0, 0, '2026-02-01 17:56:23', '2026-03-02 16:42:15', 0, 1);
INSERT INTO `post` VALUES (37, '测试延迟发信插件', '测试延迟发信插件:+1:\n', '1', 0, b'0', b'0', 0, 0, '2026-02-01 19:09:26', '2026-02-01 19:12:38', 1, 1);
INSERT INTO `post` VALUES (38, '测试延迟发信插件', '测试延迟发信插件:tada:️\n', '1', 5, b'0', b'0', 1, 0, '2026-02-01 19:13:02', '2026-03-02 13:57:24', 0, 1);
INSERT INTO `post` VALUES (39, '极力推荐！！！工大附近美食', '洛馍村无敌\n\n还有手抓饼\n\n还有还有啥忘了\n', '19', 5, b'0', b'0', 0, 0, '2026-03-02 13:54:21', '2026-03-06 14:29:00', 0, 1);
INSERT INTO `post` VALUES (40, '图书馆闭馆时间', '日常图书馆闭馆时间都是十点，但是九点半就开始放闭馆音乐了\n', '19', 1, b'0', b'0', 0, 0, '2026-03-02 14:01:12', '2026-03-02 14:02:24', 0, 1);
INSERT INTO `post` VALUES (41, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 2, b'0', b'0', 0, 0, '2026-03-02 14:16:36', '2026-03-02 14:19:26', 1, 1);
INSERT INTO `post` VALUES (42, '张三我要杀了你', '张三我要杀了你\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:19:35', '2026-03-02 14:20:03', 1, 1);
INSERT INTO `post` VALUES (43, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:20:38', '2026-03-02 14:20:51', 1, 1);
INSERT INTO `post` VALUES (44, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:20:59', '2026-03-02 14:21:44', 1, 1);
INSERT INTO `post` VALUES (45, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 1, b'0', b'0', 0, 0, '2026-03-02 14:21:54', '2026-03-02 14:22:24', 1, 1);
INSERT INTO `post` VALUES (46, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 1, b'0', b'0', 0, 0, '2026-03-02 14:22:26', '2026-03-02 14:22:48', 1, 1);
INSERT INTO `post` VALUES (47, '张三你他妈的就是个傻逼张三你他妈的就是个傻逼草泥马', '张三你他妈的就是个傻逼草你妈\n', '19', 1, b'0', b'0', 0, 0, '2026-03-02 14:22:58', '2026-03-02 14:24:24', 1, 1);
INSERT INTO `post` VALUES (48, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:24:34', '2026-03-02 14:25:18', 1, 1);
INSERT INTO `post` VALUES (49, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:25:23', '2026-03-02 14:25:54', 1, 1);
INSERT INTO `post` VALUES (50, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:26:13', '2026-03-02 14:26:26', 1, 1);
INSERT INTO `post` VALUES (51, '张三你他妈的就是个傻逼', '张三你他妈的就是个傻逼\n', '19', 0, b'0', b'0', 0, 0, '2026-03-02 14:27:41', '2026-03-02 14:27:41', 0, 3);
INSERT INTO `post` VALUES (52, '期末高数复习终极指南与踩坑记录', '高数考试最重要的就是刷历年真题和课后习题。第一章到第三章的极限和微积分是基础，必须把洛必达法则和泰勒公式背烂，这两部分占了整张卷子大约30%的分数。第四章后面的级数部分，有很多同学反映听不懂，如果实在不理解就去B站看宋浩老师的视频，讲得非常平易近人。考前一周一定要自己模拟考两次，严格控制时间在两个小内完成。另外，建议大家去图书馆沉浸式复习，效率比在宿舍高很多。宿舍的床铺和游戏机是学习的头号公敌！最后祝大家都能期末不挂科！', '1', 121, b'0', b'1', 0, 0, '2026-03-04 20:14:56', '2026-03-06 11:02:49', 0, 1);
INSERT INTO `post` VALUES (53, '莲花街校区第二食堂美食排坑与强推', '二餐一楼的黄焖鸡米饭绝绝子，鸡肉很嫩而且土豆炖得特别软糯，微辣最下饭，只要13块钱就能吃得非常饱。但是千万不要去点那个过桥米线，汤底全是科技与狠活的味精味，吃完下午口干舌燥一直想喝水。二楼的自选快餐性价比很高，两荤一素只要12块钱，而且阿姨打菜从来不手抖，有时候还会多给一块红烧肉。建议大家避开12点到12点半的高峰期，不然排队要十分钟，找座位还要找十分钟。晚上想吃夜宵的话可以去一餐旁边的小吃街，烤冷面和淀粉肠也是一绝。', '1', 86, b'0', b'0', 0, 0, '2026-03-04 20:14:56', '2026-03-06 10:32:49', 0, 1);
INSERT INTO `post` VALUES (54, '关于校园网频繁断连和网速慢的终极解决方法', '最近很多同学反映宿舍校园网动不动就断连，特别是在打游戏的时候突然失去连接，非常搞心态。如果是锐捷客户端一直提示认证失败，建议先在客户端上点击“修复网络”，然后清理一下DNS缓存（cmd输入ipconfig /flushdns），最后重启电脑。如果是连上了但是没有网速，可能是DNS被污染了，可以尝试把电脑的网络DNS改成114.114.114.114或者阿里云的223.5.5.5。另外，提醒各位大一的新生，晚上11点半之后宿舍断网是学校的统一规定，为了保证大家休息，周末则会延长到12点断网。如果经过上述设置还是不行，可以直接打电话给网络中心报修，电话是666888，通常第二天师傅就会上门。', '1', 317, b'1', b'0', 1, 0, '2026-03-04 20:14:56', '2026-03-06 14:29:00', 0, 1);
INSERT INTO `post` VALUES (55, '计算机学院考研光荣榜及复习经验干货分享', '学长今年成功上岸本校计算机科学与技术专业研究生！给大家分享一点干货。专业课408非常硬核，建议基础一般的同学最晚在暑假前过完第一遍数据结构和计算机组成原理。操作系统和计网可以稍微晚一点，在秋季学期开始前过完一遍即可。复习的时候千万不要死磕王道的课后算法大题，先做单项选择题巩固基础知识点，算法题后期可以背诵常考的模板。政治我是全程跟的徐涛老师的强化班，年底肖四肖八出来之后一定要背熟，不仅要背答案还要背逻辑。还有，找个靠谱的研友非常重要，大家可以互相监督，避免三天打鱼两天晒网。资料的话，可以直接去南门外的那家天天打印店找历年真题，老板那里资料很全。', '1', 512, b'0', b'1', 0, 0, '2026-03-04 20:14:56', '2026-03-06 14:29:00', 0, 1);
INSERT INTO `post` VALUES (56, 'Java后端开发秋招面经与简历修改建议', '今年秋招真的是地狱难度，投了100多份简历才拿到3个Offer。给正在准备春招或者明年秋招的学弟学妹们几个建议。第一，简历不要写精通某某技术，写“熟悉”或者“掌握”即可，不然面试官会往死里问底层源码。第二，项目经验一定要有深度，不能只是普通的CRUD（增删改查）。比如我们社区的项目，你就可以把Elasticsearch的倒排索引原理、RAG怎么用大模型提效、RabbitMQ怎么保证消息不丢失这些难点写进去。第三，八股文必须背熟，尤其是Redis的底层数据结构（ZSet的跳表）、MySQL的索引原理（B+树）和锁机制（MVCC）。第四，力扣（LeetCode）上的剑指Offer和Hot 100一定要刷至少两遍，手撕代码的时候千万不要慌，先和面试官沟通思路再写。', '1', 421, b'0', b'1', 0, 0, '2026-03-04 20:14:56', '2026-03-04 22:00:16', 0, 1);
INSERT INTO `post` VALUES (57, '寻物启事：在图书馆二楼遗失一串钥匙，急！', '今天上午10点左右，在图书馆二楼考研自修室，靠窗的第三排座位，我不小心遗失了一串钥匙。钥匙上面有一个皮卡丘的橡胶钥匙扣、一把电动车的车钥匙，还有一张蓝色的门禁卡。如果有捡到的好心同学请务必联系我，必有重谢（请你喝一周的奶茶）！我的手机号是：138xxxx1234，微信同号。这串钥匙对我非常重要，宿舍进不去了，电动车也开不走，现在正可怜巴巴地在舍友床上流浪。拜托大家帮忙留意一下！', '1', 34, b'0', b'0', 0, 0, '2026-03-04 20:14:56', '2026-03-06 17:32:04', 0, 1);
INSERT INTO `post` VALUES (58, '新生入学必看：最全的校园防骗指南', '每到开学季，总会有一些不法分子盯上刚刚步入大学校园、社会经验不足的大一新生。作为辅导员，我在这里提醒大家注意防范常见的几种骗局。第一种是“推销伪劣商品”，有人进宿舍推销各种英语学习报纸、便宜的化妆品或者是校园电话卡，切记不要买，正规商品请去学校超市购买。第二种是“冒充老乡借钱”，遇到自称老乡的人以钱包被偷等理由借钱的，一律拒绝。第三种是“兼职刷单陷阱”，群里发的刷单赚零花钱的信息绝对是诈骗，不要抱有侥幸心理。第四种是“冒充学校老师收费”，学校的所有收费项目都会通过官方渠道通知并使用校园统一支付平台，任何要求转账到个人微信或支付宝的都是骗子！遇到可疑情况，请立刻联系你的辅导员或者拨打校园保卫处电话555110。', '1', 216, b'1', b'1', 0, 0, '2026-03-04 20:14:56', '2026-03-04 21:50:16', 0, 1);
INSERT INTO `post` VALUES (59, '周末去哪里玩？市中心Citywalk路线推荐', '不想周末一直宅在宿舍打游戏？给大家推荐一条亲测好玩的市中心Citywalk路线！早上9点从学校南门出发坐地铁1号线，大概40分钟到达市中心的博物馆站。上午先去市博物馆看一场免费的文物展览，感受一下历史文化底蕴（需要提前在微信公众号上预约）。中午去博物馆旁边的老街吃本地特色的小吃，推荐那家排队很长的陈记生煎包。下午步行前往人民公园散步，那里的湖景很不错，秋天还有银杏叶可以拍照打卡。傍晚去市中心的商业街逛逛，有很多潮流快闪店和谷子店。晚饭可以在商业街吃一顿火锅，吃完再坐地铁回学校，完美充实的一天！人均消费在100元以内，非常适合大学生特种兵打卡。', '1', 156, b'0', b'0', 0, 0, '2026-03-04 20:14:56', '2026-03-04 20:14:56', 0, 1);
INSERT INTO `post` VALUES (60, '参加了百团大战，吉他社和动漫社哪个比较好？', '大一萌新一枚，这周末学校操场举办了百团大战，看着眼花缭乱的社团真的不知道怎么选。我目前比较感兴趣的是吉他社和动漫社。我以前没有学过乐器，但是很想在大学里学会弹吉他，不知道吉他社对零基础的新手友不友好？有没有学长学姐带？动漫社的话，我平时经常看番，也很想尝试一下Cosplay，但是害怕自己社恐融入不进去。有没有这两个社团的学长学姐来现身说法一下？另外社团的会费大概是多少呀？如果有其他好玩的社团也欢迎大家疯狂安利给我！', '1', 89, b'0', b'0', 0, 0, '2026-03-04 20:14:56', '2026-03-04 20:14:56', 0, 1);
INSERT INTO `post` VALUES (61, '求助：大三准备去实习，简历该怎么写？', '各位大佬好，我是软件工程专业大三的学生。下学期马上就要出去找实习了，现在看着一片空白的简历非常焦虑。我在学校里的成绩处于中游（GPA 3.2），除了大一在学生会当过一次干事之外，没有什么社团干部的经历充门面。项目经验也只有两门专业课的大作业，一个是图书管理系统，另一个是用Python写的简单爬虫。现在感觉自己的竞争力好弱。请问我该怎么优化我的简历才能在实习招聘中脱颖而出？是应该在这几个月恶补一个开源项目写进简历里，还是应该多刷算法题？希望有经验的学长学姐给点指点，听劝，万分感谢！', '1', 139, b'0', b'1', 0, 0, '2026-03-04 20:14:56', '2026-03-06 15:26:36', 0, 1);

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `topic_id`(`topic_id` ASC) USING BTREE,
  CONSTRAINT `post_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `post_tag_ibfk_2` FOREIGN KEY (`topic_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 128 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '话题-标签 中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
INSERT INTO `post_tag` VALUES (102, 19, 3);
INSERT INTO `post_tag` VALUES (106, 14, 1);
INSERT INTO `post_tag` VALUES (108, 7, 25);
INSERT INTO `post_tag` VALUES (109, 18, 23);
INSERT INTO `post_tag` VALUES (114, 7, 32);
INSERT INTO `post_tag` VALUES (115, 13, 2);
INSERT INTO `post_tag` VALUES (118, 20, 7);
INSERT INTO `post_tag` VALUES (119, 21, 35);
INSERT INTO `post_tag` VALUES (121, 14, 33);
INSERT INTO `post_tag` VALUES (122, 22, 36);
INSERT INTO `post_tag` VALUES (123, 23, 39);
INSERT INTO `post_tag` VALUES (124, 24, 40);
INSERT INTO `post_tag` VALUES (127, 17, 27);

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织机构' ROW_FORMAT = COMPACT;

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
INSERT INTO `sys_dept` VALUES (10, '数据科学与大数据技术', 2, 3, 'admin', 1, '2023-06-21 20:15:11', NULL, 0);
INSERT INTO `sys_dept` VALUES (11, '通信工程', 5, 3, '王倩倩', 1, '2023-06-21 20:15:44', NULL, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', 'admin', '系统管理员', '2021-05-31 18:09:18', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role` VALUES (2, '普通管理员', 'common', '普通管理员', '2021-06-01 08:38:40', '2023-05-26 22:46:25', 0);
INSERT INTO `sys_role` VALUES (3, '用户管理员', 'manager', '用户管理员', '2022-06-08 17:39:04', '2023-06-19 17:28:09', 0);
INSERT INTO `sys_role` VALUES (4, '审帖员', 'article', '审帖员，审核帖子是否违规', '2023-06-19 17:19:08', '2023-06-19 17:19:08', 0);
INSERT INTO `sys_role` VALUES (5, '审核员', 'tip', '每日一句审核员', '2023-06-20 10:56:34', '2023-06-20 10:56:34', 0);

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
  `dept_id` bigint NOT NULL DEFAULT 1 COMMENT '部门id',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1：正常 0：停用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', 'admin', 'admin@cugcg.site', 1, 'https://linux.do/user_avatar/linux.do/neo/144/12_2.png', 1, '我是超级管理员', 1, '2021-05-31 18:08:43', '2026-03-06 10:20:14', 0);
INSERT INTO `sys_user` VALUES (2, '王倩倩', '96e79218965eb72c92a549dd5a330112', '王倩倩', 'wqq@cugcg.site', 0, 'community/c0799177259e463bb88f5fc541a9d62c.jpeg', 1, '普通管理员', 1, '2022-02-08 10:35:38', '2026-03-06 10:20:17', 0);
INSERT INTO `sys_user` VALUES (3, '王刚', '96e79218965eb72c92a549dd5a330112', '王刚', 'wanggang@cugcg.site', 1, 'community/c1c8ec3281914974b8e0d86585cd3412.jpeg', 1, '审帖员', 1, '2022-05-24 11:05:40', '2026-03-06 10:20:31', 0);
INSERT INTO `sys_user` VALUES (4, 'auth', '96e79218965eb72c92a549dd5a330112', 'authTest', 'auth@cugcg.site', 1, 'community/31017557b28c49ccab322b9a0192d643.jpeg', 1, '用户管理员', 1, '2023-05-10 23:19:32', '2026-03-06 10:20:08', 0);
INSERT INTO `sys_user` VALUES (5, 'nice', '96e79218965eb72c92a549dd5a330112', 'nice', 'nice@cugcg.site', 0, 'community/17d0412be20e4f97b6bd67090984711d.jpeg', 1, '审核员', 1, '2023-06-04 16:08:58', '2026-03-06 10:20:11', 0);
INSERT INTO `sys_user` VALUES (15, '小弟弟', '96e79218965eb72c92a549dd5a330112', '小弟弟', 'liyuanhaovip@163.com', 1, 'community/7a16c3f55a7040c2a060ad6091f59ce3.jpeg', 5, 'little小弟弟', 1, '2023-06-05 16:47:48', '2026-03-04 21:47:47', 0);
INSERT INTO `sys_user` VALUES (18, '李元昊', '96e79218965eb72c92a549dd5a330112', 'Ephemeral', 'LiYH_0703@foxmail.com', 1, 'community/75e93cf9fa874dd897d9fd7b6761b07b.jpeg', 3, '大家吼~~~', 1, '2023-06-21 20:20:54', '2026-03-04 21:32:35', 0);
INSERT INTO `sys_user` VALUES (19, 'woshisg', '0192023a7bbd73250516f069df18b500', 'woshisg', 'liyh0703@gmail.com', 0, 'community/31430f15c31145de871b7ce4631057e4.jpeg', 1, NULL, 1, '2026-01-31 20:22:58', '2026-03-04 21:35:54', 0);
INSERT INTO `sys_user` VALUES (20, 'test003', '96e79218965eb72c92a549dd5a330112', 'test003', 'zookd086@gmail.com', 0, 'community/4ad10957c3694e1fa15b633a6fc61275.jpeg', 0, '', 1, '2026-02-01 17:29:39', '2026-03-04 21:46:01', 0);

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
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2022-01-20 20:49:37', '2023-06-05 16:25:44', 1);
INSERT INTO `sys_user_role` VALUES (2, 1, 2, '2022-05-19 10:37:27', '2022-05-24 16:55:53', 1);
INSERT INTO `sys_user_role` VALUES (5, 1, 1, '2023-06-05 16:25:44', '2023-06-19 17:22:12', 1);
INSERT INTO `sys_user_role` VALUES (6, 2, 1, '2023-06-05 16:25:44', '2023-06-19 17:22:12', 1);
INSERT INTO `sys_user_role` VALUES (7, 3, 1, '2023-06-05 16:25:44', '2023-06-19 17:22:12', 1);
INSERT INTO `sys_user_role` VALUES (8, 1, 5, '2023-06-05 16:28:02', '2023-06-05 16:28:34', 1);
INSERT INTO `sys_user_role` VALUES (9, 1, 5, '2023-06-05 16:28:34', '2023-06-05 16:31:20', 1);
INSERT INTO `sys_user_role` VALUES (10, 4, 3, '2023-06-19 17:19:30', '2023-06-22 13:39:49', 1);
INSERT INTO `sys_user_role` VALUES (11, 1, 1, '2023-06-19 17:22:12', '2023-06-19 17:22:12', 0);
INSERT INTO `sys_user_role` VALUES (12, 2, 2, '2023-06-19 17:25:10', '2023-06-22 13:39:28', 1);
INSERT INTO `sys_user_role` VALUES (13, 3, 4, '2023-06-19 17:25:16', '2023-06-22 13:40:08', 1);
INSERT INTO `sys_user_role` VALUES (14, 5, 5, '2023-06-20 10:56:53', '2023-06-22 13:40:28', 1);
INSERT INTO `sys_user_role` VALUES (15, 2, 2, '2023-06-22 13:39:28', '2023-06-22 13:39:28', 0);
INSERT INTO `sys_user_role` VALUES (16, 4, 3, '2023-06-22 13:39:49', '2023-06-22 13:39:49', 0);
INSERT INTO `sys_user_role` VALUES (17, 3, 4, '2023-06-22 13:40:08', '2023-06-22 13:40:08', 0);
INSERT INTO `sys_user_role` VALUES (18, 5, 5, '2023-06-22 13:40:28', '2023-06-22 13:40:28', 0);
INSERT INTO `sys_user_role` VALUES (19, 5, 20, '2026-02-01 18:47:03', '2026-02-01 18:47:31', 1);
INSERT INTO `sys_user_role` VALUES (20, 3, 20, '2026-02-01 18:47:31', '2026-02-01 18:48:28', 1);
INSERT INTO `sys_user_role` VALUES (21, 1, 20, '2026-02-01 18:48:28', '2026-02-01 18:48:28', 0);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '标签',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (20, '发财');
INSERT INTO `tag` VALUES (24, '图书馆');
INSERT INTO `tag` VALUES (15, '小帅哥');
INSERT INTO `tag` VALUES (21, '归来');
INSERT INTO `tag` VALUES (14, '新人报道');
INSERT INTO `tag` VALUES (19, '新鲜');
INSERT INTO `tag` VALUES (18, '时间');
INSERT INTO `tag` VALUES (16, '标签');
INSERT INTO `tag` VALUES (12, '校园');
INSERT INTO `tag` VALUES (7, '测试');
INSERT INTO `tag` VALUES (17, '照片墙');
INSERT INTO `tag` VALUES (10, '爱情');
INSERT INTO `tag` VALUES (22, '社区更新');
INSERT INTO `tag` VALUES (13, '祝福');
INSERT INTO `tag` VALUES (8, '管理员');
INSERT INTO `tag` VALUES (23, '美食');
INSERT INTO `tag` VALUES (9, '课程设计');

-- ----------------------------
-- Table structure for tip
-- ----------------------------
DROP TABLE IF EXISTS `tip`;
CREATE TABLE `tip`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `user` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '投稿人',
  `author` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '作者',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24870 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '每日赠言' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tip
-- ----------------------------
INSERT INTO `tip` VALUES (1, '多锉出快锯，多做长知识。', '小红', '佚名', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (2, '未来总留着什么给对它抱有信心的人。', '小红', '佚名', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (3, '一个人的智慧不够用，两个人的智慧用不完。', '小红', '谚语', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (4, '十个指头按不住十个跳蚤', '小红', '傣族', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (5, '言不信者，行不果。', '小红', '墨子', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (6, '攀援而登，箕踞而遨，则几数州之土壤，皆在衽席之下。', '小红', '柳宗元', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (7, '美德大都包含在良好的习惯之内。', '小红', '帕利克', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (8, '人有不及，可以情恕。', '小红', '《晋书》', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (9, '法不传六耳', '小红', '明·吴惟顺', 0, '2023-06-05 21:59:23', '2023-06-22 14:43:08');
INSERT INTO `tip` VALUES (10, '真正的朋友应该说真话，不管那话多么尖锐。', '小红', '奥斯特洛夫斯基', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (11, '时间是一切财富中最宝贵的财富', '小红', '德奥弗拉斯多', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (12, '看人下菜碟', '小红', '民谚', 0, '2023-06-05 21:59:23', '2023-06-05 22:11:32');
INSERT INTO `tip` VALUES (13, '如果不是怕别人反感，女人决不会保持完整的严肃。', '小红', '拉罗什福科', 1, '2023-06-05 21:59:23', '2023-06-06 11:14:04');
INSERT INTO `tip` VALUES (24864, '读书百遍,其义自见', 'admin', '朱熹', 0, '2023-06-06 15:52:47', '2023-06-06 15:53:02');
INSERT INTO `tip` VALUES (24867, '欲买桂花同载酒，终不似，少年游。', '匿名', '刘过', 0, '2023-06-20 10:52:16', '2023-06-20 10:52:16');
INSERT INTO `tip` VALUES (24868, '认真的思索，真诚的明辨是非，有这种态度，大概可算是善良吧', 'Ephemeral', '王小波', 0, '2023-06-21 20:44:58', '2023-06-21 20:44:58');
INSERT INTO `tip` VALUES (24869, '读书破万卷，下笔如有神', '匿名', '杜甫', 0, '2025-09-08 21:10:18', '2025-09-08 21:10:18');

-- ----------------------------
-- Table structure for tip_post
-- ----------------------------
DROP TABLE IF EXISTS `tip_post`;
CREATE TABLE `tip_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `author` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '作者',
  `content` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `postman` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '投稿人',
  `postman_id` bigint NOT NULL DEFAULT 1 COMMENT '投稿人ID',
  `post_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投稿时间',
  `is_accepted` tinyint NOT NULL DEFAULT 0 COMMENT '是否采纳（0:未处理 1:已采纳 2:已拒绝）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tip_post
-- ----------------------------
INSERT INTO `tip_post` VALUES (1, '杜甫', '读书破万卷，下笔如有神', '匿名', 1, '2023-06-20 10:06:35', 0);
INSERT INTO `tip_post` VALUES (3, '刘禹锡', '今日听君歌一曲,暂凭杯酒长精神', '匿名', 1, '2023-06-20 10:10:07', 0);
INSERT INTO `tip_post` VALUES (4, '刘过', '欲买桂花同载酒，终不似，少年游。', '匿名', 15, '2023-06-20 10:51:36', 1);
INSERT INTO `tip_post` VALUES (5, '111', '111', '111', 15, '2023-06-20 10:53:05', 2);
INSERT INTO `tip_post` VALUES (6, '王小波', '认真的思索，真诚的明辨是非，有这种态度，大概可算是善良吧', 'Ephemeral', 18, '2023-06-21 20:44:45', 1);

-- ----------------------------
-- View structure for post_count
-- ----------------------------
DROP VIEW IF EXISTS `post_count`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `post_count` AS select `post`.`id` AS `id`,count(distinct `comment`.`id`) AS `comments`,count(distinct `favorite`.`id`) AS `favorites`,count(distinct `collect`.`id`) AS `collects` from (((`post` left join `comment` on(((`post`.`id` = `comment`.`topic_id`) and (`comment`.`is_deleted` = 0)))) left join `favorite` on(((`post`.`id` = `favorite`.`favor_id`) and (`favorite`.`type` = 2)))) left join `collect` on((`post`.`id` = `collect`.`topic_id`))) where (`post`.`is_deleted` = 0) group by `post`.`id`;

-- ----------------------------
-- View structure for tag_count
-- ----------------------------
DROP VIEW IF EXISTS `tag_count`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `tag_count` AS select `tag`.`id` AS `id`,count(distinct `post_tag`.`id`) AS `count` from ((`tag` left join `post_tag` on((`tag`.`id` = `post_tag`.`tag_id`))) left join `post` on(((`post_tag`.`topic_id` = `post`.`id`) and (`post`.`is_deleted` = 0)))) group by `tag`.`id`;

SET FOREIGN_KEY_CHECKS = 1;
