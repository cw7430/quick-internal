package com.quick.chat

import com.quick.common.api.type.ResponseCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InvalidateChatMessageTest : ChatControllerTest() {

    @Test
    @DisplayName("채팅 메세지 삭제 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        delete(URL).key().auth(accessToken).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("채팅 메세지 삭제 - 인증 오류")
    fun failWithUnauthorized() {
        delete(URL).key().send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 삭제 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        delete(URL).key().auth(INVALID_TOKEN).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 삭제 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(CHAT_LOGIN_DATA)
        delete(URL).key().auth(accessToken).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }


    @Test
    @DisplayName("채팅 메세지 삭제 - 권한 오류")
    fun failWithForbidden() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        delete(URL).key().auth(accessToken).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 삭제 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        delete(URL).auth(accessToken).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$CHAT_URL/message/1"
    }
}