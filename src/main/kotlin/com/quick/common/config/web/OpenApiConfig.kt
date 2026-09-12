package com.quick.common.config.web

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.HeaderParameter
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders

@Configuration
@Profile("!prod")
class OpenApiConfig(
    @Value($$"${security.api-key}")
    private val secretApiKey: String
) {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val accessTokenScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name(HttpHeaders.AUTHORIZATION)
            .description("Access Token을 입력하세요 (Bearer 제외하고 입력)")

        val refreshTokenScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name(HttpHeaders.AUTHORIZATION)
            .description("Refresh Token을 입력하세요 (Bearer 제외하고 입력)")

        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes("access-token", accessTokenScheme)
                    .addSecuritySchemes("refresh-token", refreshTokenScheme)
            )
    }

    @Bean
    fun globalHeaderCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.paths?.values?.forEach { pathItem ->
                pathItem.readOperations()?.forEach { operation ->
                    operation.addParametersItem(
                        HeaderParameter()
                            .name("X-API-Key")
                            .description("자동 입력된 API Key")
                            .required(true)
                            .schema(StringSchema()._default(secretApiKey))
                    )
                }
            }
        }
}