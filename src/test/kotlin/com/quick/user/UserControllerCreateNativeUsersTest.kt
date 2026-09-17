package com.quick.user

import com.quick.common.api.type.ResponseCode
import com.quick.module.user.dto.request.CreateNativeUserRequestDto
import com.quick.module.user.type.Gender
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerCreateNativeUsersTest : UserControllerTest() {
    @Test
    @DisplayName("회원가입 - 성공")
    fun success() {
        post(URL).key().body(DATA).send()
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
    }

    @Test
    @DisplayName("회원가입 - 잘 못된 입력 값")
    fun failWithValidationError() {
        post(URL)
            .key().body(INVALIDATED_DATA).send()
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("회원가입 - Api Key 오류")
    fun failWithKeyError() {
        post(URL)
            .body(DATA).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("회원가입 - Email 중복")
    fun failWithDuplicatedEmail() {
        post(URL)
            .key().body(DUPLICATED_DATA).send().andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_RESOURCE.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$USER_URL/register"
        private val DATA = CreateNativeUserRequestDto.Create(
            email = "email4@email.com",
            password = "password1234!@",
            nickName = "닉네임",
            gender = Gender.F
        )
        private val INVALIDATED_DATA = CreateNativeUserRequestDto.Create(
            email = "1234",
            password = "pw",
            nickName = " ",
            gender = Gender.F
        )
        private val DUPLICATED_DATA = CreateNativeUserRequestDto.Create(
            email = "email@email.com",
            password = "password1234!@",
            nickName = "닉네임",
            gender = Gender.M
        )
    }
}