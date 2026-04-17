# Gathering Circle (Campus Social & Intelligent Q&A Platform)

> Note: This project is not yet completed and is still under active development.

![SpringBoot](https://img.shields.io/badge/SpringBoot-2.7.x-green.svg) ![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.4.x-blue.svg) ![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg) ![Redis](https://img.shields.io/badge/Redis-6.x-red.svg) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-ff69b4.svg) ![ElasticSearch](https://img.shields.io/badge/ElasticSearch-7.x-yellowgreen.svg) ![AI](https://img.shields.io/badge/AI-DeepSeek%20%7C%20DashScope-blueviolet.svg)

## 📖 Project Overview
**Gathering Circle** is a modern campus/tech community platform. It goes beyond a traditional CRUD-style forum by deeply integrating a **high-concurrency caching architecture (Redis)**, **asynchronous message peak shaving (RabbitMQ)**, a **full-text/vector search engine (Elasticsearch)**, and a cutting-edge **production-grade RAG (Retrieval-Augmented Generation) AI question-answering architecture**.

The project aims to provide users with an immersive and highly responsive modern community experience, while also offering an intelligent AI assistant that serves as the knowledge brain of the community.

## 🎯 Core Features & Architectural Highlights

### 1. Posts and Interaction System (BBS) 💬
* **Basic interactions**: Supports post publishing, browsing, likes, favorites, shares, comments (including multi-level nested comments), and tag-based management.
* **High-concurrency optimizations**:
    * Uses **Redis Set** to implement anti-spam and anti-duplicate checks for likes/favorites, enabling in-memory **O(1)** validation.
    * Uses **Redis** cached counters to maintain post popularity, combined with **ZSet** to build a cohesive daily hot-post ranking system.
    * High-frequency core APIs (such as likes and posting) integrate a custom annotation `@RedisLock` based on AOP and Redis to implement rate limiting and oversell prevention.
* **Scheduled publishing (delayed queue)**: Cleverly uses the **RabbitMQ delayed plugin / dead-letter queue** to implement second-level accurate scheduled post publishing.

### 2. Message-Driven Notification Center (Asynchronous MQ) 📨
* Instead of relying on synchronous blocking calls, all trigger actions between users—such as likes, follows, and comments—are routed through **RabbitMQ**, and consumers asynchronously construct system notification records (`SysNotification`), greatly improving interface throughput.
* Email alerts are integrated so that approval results and security reminders can be delivered to users asynchronously.

### 3. Multimodal and Vertical Search Engine 🔍
* Replaces traditional `LIKE` fuzzy queries with **Elasticsearch** (Spring Data ES), enabling second-level full-text retrieval across posts, users, titles, and content through inverted indexes.
* Uses RabbitMQ `EsIndexMessage` to achieve near real-time dual-write consistency between MySQL business tables and Elasticsearch documents.
* Integrates **Qiniu Cloud OSS** to support cloud CDN storage for avatars and rich-text images, with seamless dynamic URL assembly.

### 4. Production-Grade RAG Intelligent AI Q&A Engine 🤖
* **Vectorized indexing**: When a post is persisted, the system calls Alibaba Cloud DashScope (`text-embedding-v4`) to convert text and image-derived content into **1024-dimensional vectors** and push them into Elasticsearch.
* **Three-step RAG retrieval-augmented generation architecture**:
    1. **Query rewriting**: DeepSeek LLM expands the user question from multiple semantic angles to solve zero-recall issues caused by incomplete wording.
    2. **Multi-path hybrid retrieval**: Executes vector similarity retrieval (ES KNN) and inverted-index keyword retrieval concurrently, then merges and deduplicates the results.
    3. **LLM reranker**: A large model scores and reranks the hybrid retrieval results to extract Top-K candidates, then assembles a prompt and asks DeepSeek to generate an answer with source references.
* **AI content safety auditing**: All newly published posts are sent through a queue to DeepSeek for automated AI compliance checks, including pornography, violence/terrorism, and sensitive content filtering.

## 🛠 Development Environment & Deployment

### Requirements
* JDK: `17`
* Database: `MySQL 8.x`
* Cache: `Redis 6.x`
* Message middleware: `RabbitMQ 3.x+` (the required delayed-message plugin must be enabled)
* Search engine: `Elasticsearch 7.x`
* Build tool: `Maven 3.x`

### Quick Start 🚀
1. Import `haut_community.sql` to initialize the database schema in MySQL.
2. Configure your `application-dev.yml` (or create a new `application-local.yml`):
    * Update the username and password under `spring.datasource`.
    * Modify the host or password under `spring.redis`.
    * Configure your email account under `spring.mail`.
    * (Optional) Update RabbitMQ and Elasticsearch connection addresses to match your local environment.
3. Obtain external service API keys **[Important]**:
    * Fill in your own Qiniu Cloud `accessKey`, `secretKey`, `bucket`, and `domain` in `FileServiceImpl`.
    * Visit the [DeepSeek Open Platform](https://platform.deepseek.com/) and [Alibaba Cloud DashScope](https://bailian.console.aliyun.com/) to apply for the latest API keys, then configure them in the `ai.chat` and `ai.embedding` sections of `application-dev.yml`.
4. Start the backend main application class.
5. **Test accounts**: The initialization script includes several preset test accounts. See the database for details. The default initial password is `111111`.

## 📸 Feature Structure Diagram
*(Placeholder for the original business architecture diagram)*
![image](https://github.com/Ephemeral2333/HAUT_Community/assets/88269111/3147df80-4ce9-4672-80e6-db2be0947184)
