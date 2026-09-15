package com.quick.module.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(name = "NativeLoginRequest")
data class NativeLoginRequestDto(
    @get:Schema(description = "이메일", example = "example@example.com")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @get:Schema(description = "비밀번호", example = "examplepw1234!@")
    val password: String,

    @get:Schema(description = "장기 갱신", example = "false")
    val isAuto: Boolean = false
)
