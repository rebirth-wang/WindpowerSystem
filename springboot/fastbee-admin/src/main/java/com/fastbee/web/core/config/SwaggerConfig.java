package com.fastbee.web.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastbee.common.config.RuoYiConfig;

@Configuration
@EnableAutoConfiguration
public class SwaggerConfig
{
    @Autowired
    private RuoYiConfig ruoyiConfig;

    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI()
                .info(new Info()
                        .title("FastBee物联网平台接口文档")
                        .description("描述：FastBee物联网平台")
                        .version("版本号:" + ruoyiConfig.getVersion())
                        .contact(new Contact().name(ruoyiConfig.getName())))
                .schemaRequirement("Authorization", createBearerScheme());
    }

    private SecurityScheme createBearerScheme()
    {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .in(SecurityScheme.In.HEADER);
    }
}
