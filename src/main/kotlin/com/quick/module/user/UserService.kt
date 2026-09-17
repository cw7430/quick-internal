package com.quick.module.user

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.config.security.JwtProvider
import com.quick.common.config.security.JwtUtil
import com.quick.module.user.dto.request.CreateNativeUserRequestDto
import com.quick.module.user.dto.request.LogoutRequestDto
import com.quick.module.user.dto.request.NativeLoginRequestDto
import com.quick.module.user.dto.request.RefreshRequestDto
import com.quick.module.user.dto.request.UpdateNativeUserRequestDto
import com.quick.module.user.dto.response.LoginResponseDto
import com.quick.module.user.dto.vo.UserVo
import com.quick.module.user.repository.UserJooqRepository
import com.quick.module.user.type.AuthType
import com.quick.module.user.type.Gender
import com.quick.module.user.type.Role
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

private val log = KotlinLogging.logger {}

@Service
class UserService(
    private val jwtProvider: JwtProvider,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val userJooqRepository: UserJooqRepository
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

        userJooqRepository.createRefreshToken(
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

    @Transactional
    fun nativeLogin(reqDto: NativeLoginRequestDto): LoginResponseDto {
        val user = userJooqRepository.findLoginInfoByEmail(reqDto.email)
            ?: throw CustomException(ResponseCode.LOGIN_ERROR)

        if (!passwordEncoder.matches(reqDto.password, user.passwordHash)) {
            throw CustomException(ResponseCode.LOGIN_ERROR)
        }

        log.info { "Login In successfully for user ID:${user.userId}" }

        return issueTokensAndBuild(user, reqDto.isAuto)
    }

    @Transactional
    fun refresh(req: HttpServletRequest, reqDto: RefreshRequestDto): LoginResponseDto {
        val refreshToken = jwtUtil.extractToken(req)
        val userId = jwtUtil.extractUserIdFromRefreshToken(refreshToken)

        if (!userJooqRepository.existRefreshTokenByUserIdAndToken(userId, refreshToken)) {
            throw CustomException(ResponseCode.UNAUTHORIZED)
        }

        userJooqRepository.deleteRefreshTokenByRefreshToken(refreshToken)

        val user = userJooqRepository.findRefreshInfoByUserId(userId)
            ?: throw CustomException(ResponseCode.UNAUTHORIZED)

        log.info { "Refresh successfully for user ID:${user.userId}" }

        return issueTokensAndBuild(user, reqDto.isAuto)
    }

    @Transactional
    fun logout(reqDto: LogoutRequestDto) {
        val refreshToken = reqDto.refreshToken ?: return
        userJooqRepository.deleteRefreshTokenByRefreshToken(refreshToken)
    }

    @Transactional
    fun checkEmail(reqDto: CreateNativeUserRequestDto) {
        if (userJooqRepository.existNativeUsersByEmail(reqDto.email)) {
            throw CustomException(ResponseCode.DUPLICATE_RESOURCE)
        }

        log.info { "Check Email successfully for Email:${reqDto.email}" }
    }

    @Transactional
    fun createNativeUsers(reqDto: CreateNativeUserRequestDto.Create): LoginResponseDto {
        checkEmail(reqDto)
        val users = userJooqRepository.createUsersAndGetUsers(
            authType = AuthType.NATIVE,
            nickName = reqDto.nickName,
            gender = reqDto.gender
        ) ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)

        val userId = users.id ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)

        userJooqRepository.createNativeUsers(
            id = userId,
            email = reqDto.email,
            passwordHash = passwordEncoder.encode(reqDto.password)!!
        )

        val loginInfo = UserVo.Public(
            userId,
            authType = AuthType.from(users.authType)
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
            nickName = users.nickName
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
            gender = Gender.from(users.gender)
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
            role = Role.from(users.role)
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
            createdAt = users.createdAt
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
            updatedAt = users.updatedAt
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        )

        log.info { "Register successfully for user ID:${userId}" }

        return issueTokensAndBuild(loginInfo, isAuto = false)
    }

    @Transactional
    fun updatePassword(reqDto: UpdateNativeUserRequestDto.Password) {
        val userId = jwtUtil.getCurrentUserId()
        val formalPassword = userJooqRepository.findPasswordByUserId(userId)
            ?: throw CustomException(ResponseCode.UNAUTHORIZED)
        if (!passwordEncoder.matches(reqDto.password, formalPassword)) {
            throw CustomException(ResponseCode.PASSWORD_ERROR)
        }
        if (passwordEncoder.matches(reqDto.newPassword, formalPassword)) {
            throw CustomException(ResponseCode.DUPLICATE_RESOURCE)
        }
        userJooqRepository.updatePassword(
            userId,
            passwordEncoder.encode(reqDto.newPassword)!!
        )
        log.info { "Update Password successfully for user ID:${userId}" }
    }

    @Transactional
    fun updateNickName(reqDto: UpdateNativeUserRequestDto.NickName) {
        val userId = jwtUtil.getCurrentUserId()
        if (!userJooqRepository.existUsersByUserId(userId)) {
            throw CustomException(ResponseCode.UNAUTHORIZED)
        }
        userJooqRepository.updateNickName(userId, reqDto.newNickName)
        log.info { "Update NickName successfully for user ID:${userId}" }
    }
}