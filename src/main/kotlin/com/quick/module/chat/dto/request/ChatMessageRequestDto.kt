package com.quick.module.chat.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.Instant

interface ChatMessageRequestDto {
    @Schema(name = "ChatMessageListRequest")
    data class GetList(
        @get:Schema(description = "일련번호", example = "1")
        val chatMessageId: Long?,
        @get:Schema(description = "등록된 날짜", example = "2026-01-01T00:00:00Z")
        val createdAt: Instant?,
        @get:Schema(description = "페이지 사이즈", example = "10")
        val size: Int = 10
    ) : ChatMessageRequestDto

    @Schema(name = "CreateAndUpdateChatMessageRequest")
    data class Message(
        @field:NotBlank(message = "메세지를 입력해주세요.")
        @get:Schema(description = "메세지", example = "메세지")
        val message: String
    ) : ChatMessageRequestDto

    @Schema(name = "UpdateChatMessageReadRequest")
    data class Read(
        @get:Schema(description = "메세지 일련번호", example = "[1, 2]")
        val chatMessageIdList: List<Long>
    ) : ChatMessageRequestDto
}