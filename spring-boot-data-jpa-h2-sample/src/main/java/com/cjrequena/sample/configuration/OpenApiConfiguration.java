package com.cjrequena.sample.configuration;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
public class OpenApiConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String version;

    @Bean
    public OpenAPI customOpenApi() {
        PrimitiveType.customClasses().put(LocalTime.class.getName(), PrimitiveType.PARTIAL_TIME);
        return new OpenAPI()
          .components(new Components())
          .info(new Info()
            .title(applicationName)
            .version(version)
            .termsOfService("")
            .contact(new Contact().name(""))
            .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    @Bean
    public GroupedOpenApi groupOpenApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .packagesToScan("com.cjrequena.sample.web.api")
                .build();
    }
}
