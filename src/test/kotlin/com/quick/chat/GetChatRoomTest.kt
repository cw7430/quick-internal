package com.quick.chat

import com.quick.common.api.type.ResponseCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetChatRoomTest : ChatControllerTest() {

    @Test
    @DisplayName("채팅방 상세보기 불러오기 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        get(URL).key().auth(accessToken).send()
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("채팅방 목록 불러오기 - 인증 오류")
    fun failWithUnauthorized() {
        get(URL).key().send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 상세보기 불러오기 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        get(URL).key().auth(INVALID_TOKEN).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 상세보기 불러오기 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(CHAT_LOGIN_DATA)
        get(URL).key().auth(accessToken).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 상세보기 불러오기 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        get(URL).auth(accessToken).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 상세보기 불러오기 - 찾을 수 없는 요소")
    fun failWithNotFound() {
        val accessToken = authTestUtil.getTestToken(CHAT_LOGIN_DATA).accessToken
        get(INVALID_URL).key().auth(accessToken).send()
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ResponseCode.RESOURCE_NOT_FOUND.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$CHAT_URL/room/1"
        private const val INVALID_URL = "$CHAT_URL/room/2"
    }
}