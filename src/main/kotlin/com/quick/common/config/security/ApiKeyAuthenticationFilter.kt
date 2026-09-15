package com.quick.common.config.security

import com.quick.common.api.response.ErrorResponseDto
import com.quick.common.api.type.ResponseCode
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import tools.jackson.databind.ObjectMapper
import java.io.IOException


@Component
class ApiKeyAuthenticationFilter(
    private val objectMapper: ObjectMapper,
    @Value($$"${security.api-key}") private val secretApiKey: String
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()

    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
            return true
        }

        val requestURI = request.requestURI
        return EXCLUDED_URIS.stream()
            .anyMatch { pattern: String? -> pathMatcher.match(pattern!!, requestURI) }
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKeyHeader = request.getHeader("X-API-Key")

        if (secretApiKey != apiKeyHeader) {
            val responseCode = ResponseCode.KEY_ERROR

            val errorResponse: ErrorResponseDto = ErrorResponseDto.from(responseCode)

            response.contentType = "application/json;charset=UTF-8"
            response.status = responseCode.status.value()
            response.writer.write(objectMapper.writeValueAsString(errorResponse))

            return
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        private val EXCLUDED_URIS = listOf(
            "/api/v1/health-check",
            "/swagger-ui/**",
            "/api-docs/**",
            "/api/v1/user/logout"
        )
    }
}
