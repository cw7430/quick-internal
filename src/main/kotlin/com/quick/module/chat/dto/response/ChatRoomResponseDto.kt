package com.quick.module.chat.dto.response

import com.quick.common.type.YN
import com.quick.module.user.type.Gender
import java.time.Instant

interface ChatRoomResponseDto {
    data class ChatMember(
        val chatMemberId: Long,
        val userId: Long,
        val nickName: String,
        val gender: Gender,
        val accepted: YN
    )

    data class ListData(
        val chatRoomId: Long,
        val updatedAt: Instant,
        val chatMemberId: Long,
        val userId: Long,
        val nickName: String,
        val gender: Gender,
        val accepted: YN
    ): ChatRoomResponseDto

    data class DetailData(
        val chatRoomId: Long,
        val updatedAt: Instant,
        val memberList: List<ChatMember>
    ): ChatRoomResponseDto
}