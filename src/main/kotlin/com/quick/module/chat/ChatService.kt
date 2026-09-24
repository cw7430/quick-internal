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
        chatJooqRepository.updateChatMemberAcceptedByChatMemberId(chatMemberId)
        chatJooqRepository.updateChatRoomUpdatedAtByChatMemberId(chatMemberId)
        log.info { "Accept Chat Room successfully for UserId:${userId}, ChatMemberId:${chatMemberId}" }
    }

    @Transactional
    fun invalidateChatRoom(chatRoomId: Long) {
        val userId = jwtUtil.getCurrentUserId()
        if (!chatJooqRepository.existChatMemberByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw CustomException(ResponseCode.FORBIDDEN)
        }
        chatJooqRepository.updateChatRoomInvalidByChatRoomId(chatRoomId)
        log.info { "Invalidate Chat Room successfully for UserId:${userId}, ChatRoomId:${chatRoomId}" }
    }

    @Transactional
    fun sendChatMessage(chatMemberId: Long, reqDto: ChatMessageRequestDto.Message) {
        val userId = jwtUtil.getCurrentUserId()
        if (!chatJooqRepository.existChatMemberByChatMemberIdAndUserId(chatMemberId, userId)) {
            throw CustomException(ResponseCode.FORBIDDEN)
        }
        chatJooqRepository.createChatMessageByChatMemberId(chatMemberId, reqDto.message)
        chatJooqRepository.updateChatRoomUpdatedAtByChatMemberId(chatMemberId)
        log.info { "Send Chat Message successfully for UserId:${userId}, ChatMemberId:${chatMemberId}" }
    }

    @Transactional
    fun updateChatMessage(chatMessageId: Long, reqDto: ChatMessageRequestDto.Message) {
        val userId = jwtUtil.getCurrentUserId()
        if (!chatJooqRepository.existChatMessageByChatMessageIdAndUserId(chatMessageId, userId)) {
            throw CustomException(ResponseCode.FORBIDDEN)
        }
        chatJooqRepository.updateChatMessageByChatMessageId(chatMessageId, reqDto.message)
        chatJooqRepository.updateChatRoomUpdatedAtByChatMessageId(chatMessageId)
        log.info { "Update Chat Message successfully for UserId:${userId}, ChatMessageId:${chatMessageId}" }
    }

    @Transactional
    fun updateChatMessageRead(reqDto: ChatMessageRequestDto.Read) {
        val userId = jwtUtil.getCurrentUserId()
        chatJooqRepository.updateChatMessageReadByChatMessageIdList(reqDto.chatMessageIdList)
        log.info { "Update Chat Message Read successfully for UserId:${userId}, ChatMessageIdList:${reqDto.chatMessageIdList}" }
    }

    @Transactional
    fun invalidateChatMessage(chatMessageId: Long) {
        val userId = jwtUtil.getCurrentUserId()
        if (!chatJooqRepository.existChatMessageByChatMessageIdAndUserId(chatMessageId, userId)) {
            throw CustomException(ResponseCode.FORBIDDEN)
        }
        chatJooqRepository.updateChatMessageInvalidByChatMessageId(chatMessageId)
        log.info { "Invalidate Chat Message successfully for UserId:${userId}, ChatMessageId:${chatMessageId}" }
    }
}