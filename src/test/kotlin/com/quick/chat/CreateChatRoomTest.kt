package com.quick.chat

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.type.YN
import com.quick.module.chat.repository.ChatJooqRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreateChatRoomTest : ChatControllerTest() {

    @Autowired
    private lateinit var chatJooqRepository: ChatJooqRepository

    @Test
    @DisplayName("채팅방 요청 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        post(URL).key().auth(accessToken).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("채팅방 요청 - 인증 오류")
    fun failWithUnauthorized() {
        post(URL).key().send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 요청 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        post(URL).key().auth(INVALID_TOKEN).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 요청 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(USER_LOGIN_DATA)
        post(URL).key().auth(accessToken).send()
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 요청 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        post(URL).auth(accessToken).send()
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 요청 - 본인에게 보낸 요청")
    fun failWithDuplicateConflict() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        post(CONFLICT_URL).key().auth(accessToken).send()
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ResponseCode.CONFLICT.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("채팅방 요청 - 중복된 채팅방")
    fun failWithDuplicateResource() {
        val chatRoom =
            chatJooqRepository.createChatRoomAndGet() ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        val chatRoomId = chatRoom.id ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        chatJooqRepository.createChatMember(chatRoomId, 2, YN.Y)
        chatJooqRepository.createChatMember(chatRoomId, 4, YN.N)
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        post(URL).key().auth(accessToken).send()
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_RESOURCE.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$CHAT_URL/room/2"
        private const val CONFLICT_URL = "$CHAT_URL/room/4"
    }
}