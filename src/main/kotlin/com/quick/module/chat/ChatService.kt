package com.quick.module.chat

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.config.security.JwtUtil
import com.quick.common.type.YN
import com.quick.module.chat.repository.ChatJooqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class ChatService(
    private val jwtUtil: JwtUtil,
    private val chatJooqRepository: ChatJooqRepository
) {
    @Transactional
    fun createChatRoom(userId: Long) {
        val reqUserId = jwtUtil.getCurrentUserId()
        if (chatJooqRepository.existActiveChatRoomByUserId(reqUserId, userId)) {
            throw CustomException(ResponseCode.DUPLICATE_RESOURCE)
        }
        val chatRoom =
            chatJooqRepository.createChatRoomAndGet() ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        val chatRoomId = chatRoom.id ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)
        chatJooqRepository.createChatMember(chatRoomId, reqUserId, YN.Y)
        chatJooqRepository.createChatMember(chatRoomId, userId, YN.N)
        log.info { "Create Chat Room successfully for UserId:${reqUserId}, ResponseUserId:${userId}" }
    }
}