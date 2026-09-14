package com.quick.common.config.security

import com.quick.common.api.exception.CustomException
import com.quick.common.config.security.type.Role
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(private val jwtProvider: JwtProvider, private val jwtUtil: JwtUtil) :
    OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtUtil.extractTokenForFilter(request)

            if (token.isNullOrBlank()) {
                filterChain.doFilter(request, response)
                return
            }

            val isRefreshPath = "/api/v1/auth/refresh" == (request.requestURI)
            val claims = jwtProvider.getClaims(token, isRefreshPath)

            if (isRefreshPath) {
                request.setAttribute("refreshClaims", claims)
            } else {
                setAuthentication(claims)
            }
        } catch (e: CustomException) {
            request.setAttribute("exception", e.responseCode)
        } catch (e: Exception) {
            request.setAttribute("exception", e)
        }
        filterChain.doFilter(request, response)
    }

    fun setAuthentication(claims: Claims) {
        val id = claims.subject
        val roleCode = claims.get("role", String::class.java)
        val role = Role.from(roleCode)
        val authorities = listOf(SimpleGrantedAuthority(role.authority))
        val authentication =
            UsernamePasswordAuthenticationToken(id, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}