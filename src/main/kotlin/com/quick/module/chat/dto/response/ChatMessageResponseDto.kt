package com.quick.module.chat.dto.response

import com.quick.common.type.YN
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "ChatMessageListResponse")
data class ChatMessageResponseDto(
    @get:Schema(description = "메세지 일련번호", example = "1")
    val chatMessageId: Long,
    @get:Schema(description = "채팅방 멤버 일련번호", example = "1")
    val chatMemberId: Long,
    @get:Schema(description = "메세지", example = "안녕")
    val message: String,
    @get:Schema(description = "유효성 여부", example = "Y")
    val valid: YN,
    @get:Schema(description = "안 읽은 수", example = "1")
    val unread: Long,
    @get:Schema(description = "수정 여부", example = "Y")
    val updated: YN,
    @get:Schema(description = "생성일시", example = "2026-01-01T00:00:00Z")
    val createdAt: Instant,
    @get:Schema(description = "메세지 수정일시", example = "2026-01-01T00:00:00Z")
    val updatedMessageAt: Instant?,
)
