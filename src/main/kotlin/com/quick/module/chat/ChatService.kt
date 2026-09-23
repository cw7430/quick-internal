package com.quick.module.chat

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.config.security.JwtUtil
import com.quick.common.type.YN
import com.quick.module.chat.dto.request.ChatMessageRequestDto
import com.quick.module.chat.dto.request.ChatRoomRequestDto
import com.quick.module.chat.dto.response.ChatMessageResponseDto
import com.quick.module.chat.dto.response.ChatRoomResponseDto
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
    fun getChatRoomList(reqDto: ChatRoomRequestDto): List<ChatRoomResponseDto.ListData> {
        val reqUserId = jwtUtil.getCurrentUserId()
        val chatRoomList = chatJooqRepository.findChatRoomListByUserId(
            reqUserId,
            cursorUpdatedAt = reqDto.updatedAt,
            cursorChatRoomId = reqDto.chatRoomId,
            size = reqDto.size
        )

        log.info { "Get Chat Room List successfully for UserId:${reqUserId}" }

        return chatRoomList
    }

    fun getChatRoom(chatRoomId: Long): ChatRoomResponseDto.DetailData {
        val reqUserId = jwtUtil.getCurrentUserId()
        val chatRoom = chatJooqRepository.findChatRoomByChatRoomIdAndUserId(chatRoomId, reqUserId)
            ?: throw CustomException(ResponseCode.FORBIDDEN)

        log.info { "Get Chat Room successfully for UserId:${reqUserId}" }
        return chatRoom
    }

    fun getMessageList(chatRoomId: Long, reqDto: ChatMessageRequestDto.GetList): List<ChatMessageResponseDto> {
        val userId = jwtUtil.getCurrentUserId()
        val chatMessageList = chatJooqRepository.findChatMessageListByChatRoomId(
            chatRoomId,
            cursorCreatedAt = reqDto.createdAt,
            cursorChatMessageId = reqDto.chatMessageId,
            size = reqDto.size
        )

        log.info { "Get Chat Message List successfully for UserId:${userId}, ChatRoomId:${chatRoomId}" }
        return chatMessageList
    }

    @Transactional
    fun createChatRoom(userId: Long) {
        val reqUserId = jwtUtil.getCurrentUserId()
        if (userId == reqUserId) {
            throw CustomException(ResponseCode.CONFLICT)
        }
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

    @Transactional
    fun acceptChatRoom(chatMemberId: Long) {
        val userId = jwtUtil.getCurrentUserId()
        chatJooqRepository.updateChatMemberAccepted(chatMemberId)
        chatJooqRepository.updateChatRoomUpdatedAt(chatMemberId)
        log.info { "Accept Chat Room successfully for UserId:${userId}, ChatMemberId:${chatMemberId}" }
    }
}