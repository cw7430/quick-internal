package com.quick.chat

import com.quick.common.api.type.ResponseCode
import com.quick.module.chat.dto.request.ChatMessageRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SendChatMessageTest : ChatControllerTest() {

    @Test
    @DisplayName("채팅 메세지 보내기 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        post(URL).key().auth(accessToken).body(DATA).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("채팅 메세지 보내기 - 잘 못된 입력 값")
    fun failWithValidationError() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        post(URL).key().auth(accessToken).body(INVALID_DATA).send()
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 보내기 - 인증 오류")
    fun failWithUnauthorized() {
        post(URL).key().body(DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 보내기 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        post(URL).key().auth(INVALID_TOKEN).body(DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 보내기 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(CHAT_LOGIN_DATA)
        post(URL).key().auth(accessToken).body(DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 보내기 - 권한 오류")
    fun failWithForbidden() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        post(FORBIDDEN_URL).key().auth(accessToken).body(DATA).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 보내기 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        post(URL).auth(accessToken).body(DATA).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$CHAT_URL/message/1"
        private const val FORBIDDEN_URL = "$CHAT_URL/message/3"

        private val DATA = ChatMessageRequestDto.Message(
            message = "메세지"
        )
        private val INVALID_DATA = ChatMessageRequestDto.Message(
            message = "  "
        )
    }
}