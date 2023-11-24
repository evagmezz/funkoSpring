package dev.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    //https://localhost:3000/swagger-ui/index.html
    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Tienda de Funkos")
                                .version("1.0.0")
                                .description("API de la Tienda de Funkos")
                                .contact(
                                        new Contact()
                                                .name("Eva Gomez Uceda")
                                                .url("https://github.com/evagmezz")
                                )

                )
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()));
    }


    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("https")
                .pathsToMatch("/api/funkos/**"
                        , "/api/auth/signin")
                .displayName("Tienda de Funkos")
                .build();
    }
}