package com.quick.user

import com.quick.module.user.dto.request.LogoutRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LogoutTest : UserControllerTest() {

    @Test
    @DisplayName("로그아웃 - 성공")
    fun success() {
        val refreshToken = authTestUtil.getTestToken(MASTER_LOGIN_DATA).refreshToken
        val data = LogoutRequestDto(refreshToken)
        post(URL)
            .key().body(data).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("로그아웃 - 잘못 된 토큰으로 성공")
    fun successWithInvalidToken() {
        val data = LogoutRequestDto(INVALID_TOKEN)
        post(URL)
            .key().body(data).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("로그아웃 - 토큰 없이 성공")
    fun successWithoutToken() {
        val data = LogoutRequestDto(null)
        post(URL)
            .key().body(data).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("로그아웃 - Api Key 없이 성공")
    fun successWithoutKey() {
        val refreshToken = authTestUtil.getTestToken(MASTER_LOGIN_DATA).refreshToken
        val data = LogoutRequestDto(refreshToken)
        post(URL)
            .body(data).send()
            .andExpect(status().isNoContent())
    }

    companion object {
        private const val URL = "$USER_URL/logout"
    }
}