package com.quick.module.chat.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "ChatRoomRequest")
data class ChatRoomRequestDto(
    @get:Schema(description = "일련번호", example = "1")
    val chatRoomId: Long?,
    @get:Schema(description = "수정된 날짜", example = "2026-01-01T00:00:00Z")
    val updatedAt: Instant?,
    @get:Schema(description = "페이지 사이즈", example = "10")
    val size: Int = 10
)