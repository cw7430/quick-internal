package com.quick.chat

import com.quick.common.api.type.ResponseCode
import com.quick.module.chat.dto.request.ChatMessageRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UpdateChatMessageReadTest : ChatControllerTest() {

    @Test
    @DisplayName("채팅 읽기 처리 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        patch(URL).key().auth(accessToken).body(DATA).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("채팅 읽기 처리 - 인증 오류")
    fun failWithUnauthorized() {
        patch(URL).key().body(DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 읽기 처리 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        patch(URL).key().auth(INVALID_TOKEN).body(DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 읽기 처리 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(CHAT_LOGIN_DATA)
        patch(URL).key().auth(accessToken).body(DATA).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }


    @Test
    @DisplayName("채팅 메세지 읽기 처리 - 권한 오류")
    fun failWithForbidden() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch(URL).key().auth(accessToken).body(DATA).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅 메세지 읽기 처리 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        patch(URL).auth(accessToken).body(DATA).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$CHAT_URL/message/read/1"

        private val DATA = ChatMessageRequestDto.Read(
            chatMessageIdList = listOf(1L, 2L, 3L)
        )
    }
}