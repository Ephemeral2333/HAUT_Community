package com.liyh.system.config;

import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置
 * Spring Boot 3.x 使用 ES Java 客户端 8.x，兼容模式由 application.yml 中的
 * spring.elasticsearch.compatibility-mode=true 统一控制，无需手动设置请求头。
 */
@Configuration
public class ElasticsearchConfig {
}
