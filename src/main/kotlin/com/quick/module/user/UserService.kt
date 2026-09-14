package com.quick.module.user

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.config.security.JwtProvider
import com.quick.common.config.security.JwtUtil
import com.quick.module.user.dto.request.NativeLoginRequestDto
import com.quick.module.user.dto.response.LoginResponseDto
import com.quick.module.user.dto.vo.UserVo
import com.quick.module.user.repository.UserJooqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

private val log = KotlinLogging.logger {}

@Service
class UserService(
    private val jwtProvider: JwtProvider,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserJooqRepository
) {
    private fun issueTokensAndBuild(user: UserVo, isAuto: Boolean): LoginResponseDto {
        val accessClaims = jwtProvider.generateAccessToken(
            user.userId.toString(), user.role.toString()
        )
        val refreshClaims = jwtProvider.generateRefreshToken(
            user.userId.toString(), isAuto
        )
        val refreshTokenExpiresAtMs = refreshClaims.expiresAtMs
        val refreshTokenExpiresAtDate = Instant.ofEpochMilli(refreshTokenExpiresAtMs)

        userRepository.createRefreshToken(
            user.userId,
            refreshClaims.token,
            refreshTokenExpiresAtDate
        )

        return LoginResponseDto(
            accessToken = accessClaims.token,
            accessTokenExpiresAtMs = accessClaims.expiresAtMs,
            refreshToken = refreshClaims.token,
            refreshTokenExpiresAtMs = refreshTokenExpiresAtMs,
            isAuto = isAuto,
            authType = user.authType,
            nickName = user.nickName,
            gender = user.gender,
            role = user.role
        )
    }

    fun nativeLogin(reqDto: NativeLoginRequestDto): LoginResponseDto {
        val user = userRepository.findLoginInfoByEmail(reqDto.email)
            ?: throw CustomException(ResponseCode.LOGIN_ERROR)

        if (!passwordEncoder.matches(reqDto.password, user.passwordHash)) {
            throw CustomException(ResponseCode.LOGIN_ERROR)
        }

        log.info { "Login In successfully for account ID:${user.userId}" }

        return issueTokensAndBuild(user, reqDto.isAuto)
    }
}