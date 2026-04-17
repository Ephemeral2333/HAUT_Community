package com.liyh.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HAUT Community 接口文档")
                        .description("相关接口的文档")
                        .version("1.0"));
    }

    @Bean
    public WebMvcConfigurer swaggerCompatibilityConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                // 兼容旧地址，内部转发到 SpringDoc 默认的 OpenAPI 3 文档地址
                registry.addViewController("/v2/api-docs").setViewName("forward:/v3/api-docs");
            }
        };
    }
}
