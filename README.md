# Gathering Circle (校园社交与智能问答平台)

> 说明：项目暂未完成，当前仍在持续开发中。

![SpringBoot](https://img.shields.io/badge/SpringBoot-2.7.x-green.svg) ![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.4.x-blue.svg) ![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg) ![Redis](https://img.shields.io/badge/Redis-6.x-red.svg) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-ff69b4.svg) ![ElasticSearch](https://img.shields.io/badge/ElasticSearch-7.x-yellowgreen.svg) ![AI](https://img.shields.io/badge/AI-DeepSeek%20%7C%20DashScope-blueviolet.svg)

## 📖 项目简介
**Gathering Circle** 是一款现代化的高级校园/技术社区平台。项目突破了传统 CRUD 社区的局限，深度整合了 **高并发缓存架构 (Redis)**、**异步消息削峰 (RabbitMQ)**、**全文/向量搜索引擎 (Elasticsearch)**，以及当前最前沿的 **生产级 RAG (检索增强生成) AI 问答架构**。

主要致力于为用户打造沉浸式、极速响应的现代社区体验，同时提供智能 AI 助手作为社区百科大脑。

## 🎯 核心特性与架构亮点

### 1. 帖子与互动系统 (BBS) 💬
* **基础互动**：支持帖子的发布、浏览、点赞、收藏、分享、评论（多级子评论）与标签化管理。
* **高并发优化**：
    * 采用 **Redis** Set 实现防刷防抖的点赞/收藏校验（内存级别 $O(1)$ 判断）。
    * 采用 **Redis** 缓存计数器维护文章热度，结合 ZSet 实现了高内聚的每日热帖排行榜。
    * 核心高频接口（点赞/发帖）集成基于 AOP 和 Redis 的自定义注解 `@RedisLock` 实现限流防超卖。
* **定时发布 (延迟队列)**：巧妙利用 **RabbitMQ 延迟插件/死信队列**，实现了精准到秒的"定时发布"功能。

### 2. 消息驱动与通知中心 (MQ 异步化) 📨
* 摒弃同步阻塞调用，用户间的点赞、关注、评论等所有触发动作全部通过 **RabbitMQ** 路由投递，由消费者异步构建系统通知记录（SysNotification），极大提升接口吞吐量。
* 集成邮箱预警，审批结果、安全提示通过邮件异步触达用户。

### 3. 多模态与垂直级搜索引擎 🔍
* 摒弃传统的 `LIKE` 模糊查询，结合 **Elasticsearch** (Spring Data ES) 对全量帖子、用户、标题内容实施了基于倒排索引的秒级全文检索。
* 利用 RabbitMQ `EsIndexMessage` 实现 MySQL 业务表与 Elasticsearch Document 间的准实时双写一致性。
* 接入**七牛云 OSS**：支持头像、富文本图片的云端 CDN 存储，URL 无感动态拼接。

### 4. 生产级 RAG 智能 AI 问答大脑 🤖
* **向量化写入**：在帖子落库时，调用阿里云 DashScope (`text-embedding-v4`) 将图文提取为 1024 维高维向量并压入 ES。
* **RAG 三步走检索增强生成架构**：
    1. **Query 改写**：DeepSeek LLM 多角度扩展用户提问，解决语义缺失导致的零召回。
    2. **多路混合召回**：并发执行向量近似度检索 (ES KNN) 和倒排关键词检索，合并去重。
    3. **LLM Reranker (精排)**：大模型对混合召回的基线进行打分排序提取 Top-K，最后拼装 Prompt 要求 DeepSeek 生成带溯源引用的答案。
* **AI 安全审核**：所有新发布的帖子通过队列投递给 DeepSeek 进行涉黄、暴恐、敏感词的自动 AI 合规拦截。

## 🛠 开发环境与部署配置

### 依赖环境
* JDK: `17`
* 数据库: `MySQL 8.x`
* 缓存: `Redis 6.x`
* 消息中间件: `RabbitMQ 3.x+` (需开启对应延迟插件)
* 搜索引擎: `Elasticsearch 7.x`
* 构建工具: `Maven 3.x`

### 快速跑通指南 🚀
1. 导入 `haut_community.sql` 将表结构初始化到您的 MySQL 中。
2. 配置您的 `application-dev.yml` (或新建 `application-local.yml`):
    * 更改 `spring.datasource` 的账号密码。
    * 修改 `spring.redis` 的主机或密码。
    * 配置您的个人邮箱到 `spring.mail`。
    * （可选）配置 RabbitMQ 和 Elasticsearch 为您本机的连接地址。
3. 获取外部服务 API Key **[重要]**:
    * 在 `FileServiceImpl` 中填入您私有的七牛云 `accessKey` / `secretKey`、`bucket`、`domain`。
    * 前往 [DeepSeek 开放平台](https://platform.deepseek.com/) 和 [阿里云百炼](https://bailian.console.aliyun.com/) 申请最新的 API Key，并填入 `application-dev.yml` 的 `ai.chat` 和 `ai.embedding` 块。
4. 启动后端主程序类。
5. （测试账号）：初始化脚本预置了数个测试账号，详情见数据库，默认初始密码均为 `111111`。

## 📸 功能结构图
*(此处保留原业务架构图占位符)*
![image](https://github.com/Ephemeral2333/HAUT_Community/assets/88269111/3147df80-4ce9-4672-80e6-db2be0947184)
