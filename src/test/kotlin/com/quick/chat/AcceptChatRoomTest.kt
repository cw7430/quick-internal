package com.quick.chat

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.type.YN
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AcceptChatRoomTest : ChatControllerTest() {
    fun makeChatRoom(): Long {
        val myUserId = 3L
        val otherUserId = 4L
        val chatRoom =
            chatJooqRepository.createChatRoomAndGet() ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        val chatRoomId = chatRoom.id ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        chatJooqRepository.createChatMember(chatRoomId, myUserId, YN.Y)
        chatJooqRepository.createChatMember(chatRoomId, otherUserId, YN.N)
        val member =
            chatJooqRepository.findChatRoomByChatRoomIdAndUserId(chatRoomId, myUserId)
                ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        return member.memberList.stream()
            .filter { it.userId == otherUserId }
            .findFirst()
            .orElseThrow { CustomException(ResponseCode.INTERNAL_SERVER_ERROR) }
            .chatMemberId
    }

    @Test
    @DisplayName("채팅방 수락 - 성공")
    fun success() {
        val chatMemberId = makeChatRoom()
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch("$URL/$chatMemberId").key().auth(accessToken).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("채팅방 수락 - 인증 오류")
    fun failWithUnauthorized() {
        val chatMemberId = makeChatRoom()
        patch("$URL/$chatMemberId").key().send()
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 수락 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        val chatMemberId = makeChatRoom()
        patch("$URL/$chatMemberId").key().auth(INVALID_TOKEN).send()
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 수락 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val chatMemberId = makeChatRoom()
        val accessToken = authTestUtil.generateExpiredAccessToken(USER_LOGIN_DATA)
        patch("$URL/$chatMemberId").key().auth(accessToken).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 수락 - 권한 오류")
    fun failWithForbidden() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch("$URL/1").key().auth(accessToken).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 수락 - Api Key 오류")
    fun failWithKeyError() {
        val chatMemberId = makeChatRoom()
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch("$URL/$chatMemberId").auth(accessToken).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$CHAT_URL/room"
    }
}