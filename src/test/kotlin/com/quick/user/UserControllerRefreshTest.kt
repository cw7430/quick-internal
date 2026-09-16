package com.quick.user

import com.quick.common.api.type.ResponseCode
import com.quick.module.user.dto.request.RefreshRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerRefreshTest : UserControllerTest() {

    @Test
    @DisplayName("토큰 재발급 - 성공")
    fun refreshSuccess() {
        val refreshToken = authTestUtil.getTestToken(MASTER_LOGIN_DATA).refreshToken
        post(URL)
            .key().auth(refreshToken).body(DATA)
            .send().andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
    }

    @Test
    @DisplayName("토큰 재발급 - 인증 오류")
    fun refreshFailWithUnauthorized() {
        post(URL).key().body(DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("토큰 재발급 - 잘못 된 토큰")
    fun refreshFailWithInvalidToken() {
        post(URL).key()
            .auth(INVALID_TOKEN).body(DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("토큰 재발급 - 만료 된 토큰")
    fun refreshFailWithExpiredToken() {
        val refreshToken = authTestUtil.generateExpiredRefreshToken(MASTER_LOGIN_DATA)
        post(URL).key().auth(refreshToken).body(DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("토큰 재발급 - Api Key 오류")
    fun refreshFailWithKeyError() {
        val refreshToken = authTestUtil.getTestToken(MASTER_LOGIN_DATA).refreshToken
        post(URL)
            .auth(refreshToken).body(DATA)
            .send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$USER_URL/refresh"
        private val DATA = RefreshRequestDto(false)
    }
}