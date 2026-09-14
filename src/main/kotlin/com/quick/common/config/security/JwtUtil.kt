package com.quick.common.config.security

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class JwtUtil(
    private val jwtProvider: JwtProvider
) {
    fun extractTokenForFilter(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header.isNullOrBlank()) return null

        if (!header.startsWith("Bearer ", ignoreCase = true)) return null

        val token = header.substring(7).trim()
        return token.takeIf { it.isNotEmpty() }
    }

    fun extractToken(request: HttpServletRequest): String =
        extractTokenForFilter(request) ?: throw CustomException(ResponseCode.UNAUTHORIZED)

    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null
            || !authentication.isAuthenticated
            || authentication is AnonymousAuthenticationToken
        ) {
            throw CustomException(ResponseCode.UNAUTHORIZED)
        }
        return authentication.name.toLongOrNull()
            ?: throw CustomException(ResponseCode.UNAUTHORIZED)
    }

    fun extractUserIdFromRefreshToken(refreshToken: String): Long =
        jwtProvider.getClaims(token = refreshToken, isRefresh = true).subject.toLongOrNull()
            ?: throw CustomException(ResponseCode.UNAUTHORIZED)
}