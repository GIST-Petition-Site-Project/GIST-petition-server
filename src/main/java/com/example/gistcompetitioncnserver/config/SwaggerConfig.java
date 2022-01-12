package com.example.gistcompetitioncnserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
    @Value("${swagger.host:localhost:8080}")
    private String host;
    @Value("${swagger.protocol:https}")
    private String protocol;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .host(host)
                .protocols(Collections.singleton(protocol))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.gistcompetitioncnserver"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GIST 청원사이트 API")
                .version("1.0.0")
                .description("GIST 청원사이트 API 문서입니다.")
                .build();
    }
}
