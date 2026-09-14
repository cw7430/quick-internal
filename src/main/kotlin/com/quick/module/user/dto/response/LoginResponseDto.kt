package com.quick.module.user.dto.response

import com.quick.module.user.type.AuthType
import com.quick.module.user.type.Gender
import com.quick.module.user.type.Role
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "LoginResponse")
data class LoginResponseDto(
    @get:Schema(description = "Access token")
    val accessToken: String,

    @get:Schema(description = "Access token 만료시간", example = "1773042557262")
    val accessTokenExpiresAtMs: Long,

    @get:Schema(description = "Refresh token")
    val refreshToken: String,

    @get:Schema(description = "Refresh token 만료시간", example = "1773042557262")
    val refreshTokenExpiresAtMs: Long,

    @get:Schema(description = "장기 갱신", example = "false")
    val isAuto: Boolean,

    @get:Schema(description = "인증 타입", example = "NATIVE")
    val authType: AuthType,

    @get:Schema(description = "닉네임", example = "닉네임")
    val nickName: String,

    @get:Schema(description = "성별", example = "M")
    val gender: Gender,

    @get:Schema(description = "권한", example = "USER")
    val role: Role
)