package com.quick.module.user.dto.request

import com.quick.module.user.type.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

sealed interface CreateNativeUserRequestDto {
    val email: String

    @Schema(name = "CheckEmailRequest")
    data class CheckEmail(
        @field:NotBlank(message = "이메일을 입력해주세요.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        @get:Schema(description = "이메일", example = "example@example.com")
        override val email: String
    ): CreateNativeUserRequestDto

    @Schema(name = "CreateNativeUserRequest")
    data class Create(
        @field:NotBlank(message = "이메일을 입력해주세요.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        @get:Schema(description = "이메일", example = "example@example.com")
        override val email: String,

        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        @field:Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/~`]).{10,25}$",
            message = "비밀번호는 10자 이상 25자 이하이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        @get:Schema(description = "비밀번호", example = "examplepw1234!@")
        val password: String,

        @field:NotBlank(message = "닉네임을 입력해주세요.")
        @get:Schema(description = "닉네임", example = "닉네임")
        val nickName: String,

        @field:NotNull(message = "성별을 선택해주세요.")
        @get:Schema(description = "성별", example = "M")
        val gender: Gender
    ): CreateNativeUserRequestDto
}