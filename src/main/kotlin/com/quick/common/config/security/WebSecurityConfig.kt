package com.quick.common.config.security

import com.quick.common.api.exception.CustomAccessDeniedHandler
import com.quick.common.api.exception.CustomAuthenticationEntryPoint
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class WebSecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val apiKeyAuthenticationFilter: ApiKeyAuthenticationFilter,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    @Value($$"${spring.profiles.active}") activeProfile: String
) {
    private val allowedOrigins: List<String> = when (activeProfile) {
        "prod" -> listOf("https://www.quick-chat.com")
        "stage" -> listOf("https://test.quick-chat.com")
        else -> listOf("http://localhost:5137", "http://localhost:3000")
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .exceptionHandling { exception ->
                exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                exception.accessDeniedHandler(customAccessDeniedHandler)
            }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/swagger-ui/**", "/api-docs/**", "/api/v1/health-check", "/api/v1/user/login/**",
                    "/api/v1/user/logout", "/api/v1/user/refresh"
                ).permitAll()
                    .requestMatchers(
                        "/api/v1/chat/**"
                    )
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = this@WebSecurityConfig.allowedOrigins
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With", "X-API-Key")
            allowCredentials = true
            maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
        }
    }

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}