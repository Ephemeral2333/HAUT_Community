package com.liyh.agent.config;

import com.liyh.system.config.AiProperties;
import com.liyh.system.config.ElasticsearchConfig;
import com.liyh.system.config.MybatisPlusConfig;
import com.liyh.system.config.RedisConfig;
import com.liyh.system.service.serviceImpl.AiServiceImpl;
import com.liyh.system.service.serviceImpl.EsPostServiceImpl;
import com.liyh.system.service.serviceImpl.RagServiceImpl;
import com.liyh.system.utils.RedisUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AiProperties.class,
        MybatisPlusConfig.class,
        RedisConfig.class,
        ElasticsearchConfig.class,
        RedisUtil.class,
        AiServiceImpl.class,
        EsPostServiceImpl.class,
        RagServiceImpl.class
})
public class AgentDependencyConfig {
}
