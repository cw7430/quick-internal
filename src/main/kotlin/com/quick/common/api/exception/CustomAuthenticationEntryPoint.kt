package com.quick.common.api.exception

import com.quick.common.api.response.ErrorResponseDto
import com.quick.common.api.type.ResponseCode
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.io.IOException


@Component
class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    @Throws(ServletException::class, IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val exception = request.getAttribute("exception")

        val responseCode = exception as? ResponseCode ?: ResponseCode.UNAUTHORIZED

        val errorResponse = ErrorResponseDto.from(responseCode)

        response.contentType = "application/json;charset=UTF-8"
        response.status = responseCode.status.value()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

}