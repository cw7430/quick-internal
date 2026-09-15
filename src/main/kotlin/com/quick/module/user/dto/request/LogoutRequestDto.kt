package com.quick.module.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "LogoutRequest")
data class LogoutRequestDto(
    @get:Schema(description = "Refresh token")
    val refreshToken: String?
)