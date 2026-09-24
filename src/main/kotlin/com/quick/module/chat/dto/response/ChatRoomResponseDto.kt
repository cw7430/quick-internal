package com.quick.module.chat.dto.response

import com.quick.common.type.YN
import com.quick.module.user.type.Gender
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

interface ChatRoomResponseDto {
    @Schema(name = "ChatMemberList")
    data class ChatMember(
        @get:Schema(description = "채팅방 멤버 일련번호", example = "1")
        val chatMemberId: Long,
        @get:Schema(description = "계정 일련번호", example = "1")
        val userId: Long,
        @get:Schema(description = "닉네임", example = "닉네임")
        val nickName: String,
        @get:Schema(description = "성별", example = "M")
        val gender: Gender,
        @get:Schema(description = "수락여부", example = "N")
        val accepted: YN,
        @get:Schema(description = "나인지 여부", example = "Y")
        val me: YN
    )

    @Schema(name = "ChatRoomListResponse")
    data class ListData(
        @get:Schema(description = "채팅방 일련번호", example = "1")
        val chatRoomId: Long,
        @get:Schema(description = "수정된 날짜", example = "2026-01-01T00:00:00Z")
        val updatedAt: Instant,
        @get:Schema(description = "채팅방 멤버 일련번호", example = "1")
        val chatMemberId: Long,
        @get:Schema(description = "계정 일련번호", example = "1")
        val userId: Long,
        @get:Schema(description = "닉네임", example = "닉네임")
        val nickName: String,
        @get:Schema(description = "성별", example = "M")
        val gender: Gender,
        @get:Schema(description = "수락여부", example = "N")
        val accepted: YN,
        @get:Schema(description = "마지막 메세지", example = "메세지")
        val lastMessage: String?,
        @get:Schema(description = "안 읽은 수", example = "1")
        val totalUnread:Long
    ) : ChatRoomResponseDto

    @Schema(name = "ChatRoomResponse")
    data class DetailData(
        @get:Schema(description = "채팅방 일련번호", example = "1")
        val chatRoomId: Long,
        @get:Schema(description = "수정된 날짜", example = "2026-01-01T00:00:00Z")
        val updatedAt: Instant,
        @get:Schema(description = "멤버 목록")
        val memberList: List<ChatMember>
    ) : ChatRoomResponseDto
}