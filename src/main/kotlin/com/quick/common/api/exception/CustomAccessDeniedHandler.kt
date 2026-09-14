package com.quick.common.api.exception

import com.quick.common.api.response.ErrorResponseDto
import com.quick.common.api.type.ResponseCode
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.io.IOException

@Component
class CustomAccessDeniedHandler(private val objectMapper: ObjectMapper) : AccessDeniedHandler {
    @Throws(ServletException::class, IOException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val responseCode = ResponseCode.FORBIDDEN

        val errorResponse = ErrorResponseDto.from(
            responseCode
        )
        response.contentType = "application/json;charset=UTF-8"
        response.status = responseCode.status.value()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}