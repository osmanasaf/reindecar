package com.reindecar.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ReindeCAR API")
                        .version("1.0.0")
                        .description("RentACar & Leasing Operations System API")
                        .contact(new Contact()
                                .name("ReindeCAR Team")
                                .email("info@reindecar.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://reindecar.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token giriniz")));
    }

    @Bean
    public OperationCustomizer pageableOperationCustomizer() {
        return (operation, handlerMethod) -> {
            boolean hasPageable = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(p -> Pageable.class.isAssignableFrom(p.getParameterType()));

            if (hasPageable && operation.getParameters() != null) {
                operation.getParameters().removeIf(p -> 
                    "pageable".equalsIgnoreCase(p.getName()) || 
                    p.getName().startsWith("pageable."));

                operation.addParametersItem(new Parameter()
                        .name("page")
                        .description("Sayfa numarası (0'dan başlar)")
                        .in("query")
                        .required(false)
                        .schema(new IntegerSchema()._default(0).minimum(BigDecimal.ZERO))
                        .example(0));

                operation.addParametersItem(new Parameter()
                        .name("size")
                        .description("Sayfa başına kayıt sayısı")
                        .in("query")
                        .required(false)
                        .schema(new IntegerSchema()._default(20).minimum(BigDecimal.ONE).maximum(BigDecimal.valueOf(100)))
                        .example(20));

                operation.addParametersItem(new Parameter()
                        .name("sort")
                        .description("Sıralama (alan,yön) - Örn: name,asc veya createdAt,desc")
                        .in("query")
                        .required(false)
                        .schema(new StringSchema())
                        .example("createdAt,desc"));
            }

            return operation;
        };
    }
}
