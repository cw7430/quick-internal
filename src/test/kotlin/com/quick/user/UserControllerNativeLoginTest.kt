package com.quick.user

import com.quick.common.api.type.ResponseCode
import com.quick.module.user.dto.request.NativeLoginRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerNativeLoginTest : UserControllerTest() {
    @Test
    @DisplayName("로그인 - 성공")
    fun success() {
        post(URL).key().body(MASTER_LOGIN_DATA).send()
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
    }

    @Test
    @DisplayName("로그인 - 잘 못된 입력 값")
    fun failWithValidationError() {
        post(URL).key().body(INVALID_LOGIN_DATA).send()
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("로그인 - 잘못 된 아이디 또는 비밀번호")
    fun failWithLoginError() {
        post(URL).key().body(WRONG_LOGIN_DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.LOGIN_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("로그인 - Api Key 오류")
    fun failWithKeyError() {
        post(URL).body(MASTER_LOGIN_DATA).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$USER_URL/login/native"
        private val INVALID_LOGIN_DATA = NativeLoginRequestDto(
            " ",
            " ",
            false
        )
        private val WRONG_LOGIN_DATA = NativeLoginRequestDto(
            "wrong@email.com",
            "wrong-password",
            false
        )
    }
}