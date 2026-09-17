package com.quick.module.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

interface UpdateNativeUserRequestDto {
    @Schema(name = "UpdatePasswordRequest")
    data class Password(
        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        @get:Schema(description = "비밀번호", example = "examplepw1234!@")
        val password: String,

        @field:NotBlank(message = "새 비밀번호를 입력해주세요.")
        @field:Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/~`]).{10,25}$",
            message = "비밀번호는 10자 이상 25자 이하이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        @get:Schema(description = "비밀번호", example = "newexamplepw1234!@")
        val newPassword: String
    ) : UpdateNativeUserRequestDto

    @Schema(name = "UpdateNickNameRequest")
    data class NickName(
        @field:NotBlank(message = "닉네임을 입력해주세요.")
        @get:Schema(description = "닉네임", example = "닉네임")
        val newNickName: String
    ) : UpdateNativeUserRequestDto
}