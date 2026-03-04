package com.liyh.system.config;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 兼容性配置
 * Spring Boot 2.7 使用 ES 7.17 客户端，连接 ES 8.x 时需要启用兼容模式
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestClientBuilderCustomizer restClientBuilderCustomizer() {
        return builder -> builder.setDefaultHeaders(new Header[]{
                new BasicHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=7"),
                new BasicHeader("Content-Type", "application/vnd.elasticsearch+json;compatible-with=7")
        });
    }
}
