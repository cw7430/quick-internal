package com.quick.module.alarm.dto.response

import com.quick.common.type.YN
import com.quick.module.alarm.type.AlarmType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

interface AlarmResponseDto {
    @Schema(name = "ChatMessageAlarm")
    data class ChatMessageAlarm(
        @get:Schema(description = "채팅 일련번호", example = "1")
        val chatMessageId: Long,
        @get:Schema(description = "메세지 내용", example = "메세지 내용")
        val message: String
    )

    @Schema(name = "AlarmListResponse")
    data class ListData(
        @get:Schema(description = "알림 일련번호", example = "1")
        val alarmId: Long,
        @get:Schema(description = "상대방 사용자 일련번호", example = "1")
        val userId: Long,
        @get:Schema(description = "상대방 닉네임", example = "닉네임")
        val nickName: String,
        @get:Schema(description = "채팅방 일련번호", example = "1")
        val chatRoomId: Long,
        @get:Schema(description = "알림 유형", example = "CREATE")
        val alarmType: AlarmType,
        @get:Schema(description = "읽음 여부", example = "N")
        val read: YN,
        @get:Schema(description = "알림 생성일시", example = "2026-01-01T00:00:00Z")
        val createdAt: Instant,
        @get:Schema(description = "메세지 알림")
        val messageAlarm: ChatMessageAlarm?
    ) : AlarmResponseDto
}