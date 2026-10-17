package com.quick.module.alarm.dto.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.quick.module.alarm.type.AlarmType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import java.time.Instant

interface AlarmRequestDto {
    @Schema(name = "AlarmListRequest")
    data class GetList(
        @get:Schema(description = "일련번호", example = "1")
        val alarmId: Long?,
        @get:Schema(description = "알림이 수정된 날짜", example = "2026-01-01T00:00:00Z")
        val updatedAlarmAt: Instant?,
        @get:Schema(description = "페이지 사이즈", example = "10")
        val size: Int = 10
    ) : AlarmRequestDto

    @Schema(name = "CreateAlarmRequest")
    data class Alarm(
        @get:Schema(description = "상대방 사용자 일련번호", example = "1")
        val userId: Long,
        @get:Schema(description = "채팅방 일련번호", example = "1")
        val chatRoomId: Long,
        @get:Schema(description = "알림 유형", example = "CREATE")
        val alarmType: AlarmType,
        @get:Schema(description = "채팅 메세지 일련번호", example = "1")
        val chatMessageId: Long?
    ) : AlarmRequestDto {
        @get:JsonIgnore
        @get:AssertTrue(
            message = "MESSAGE 유형은 chatMessageId가 필수입니다."
        )
        @Suppress("unused")
        val isValidMessage: Boolean
            get() = alarmType != AlarmType.MESSAGE || chatMessageId != null
    }

    @Schema(name = "UpdateAlarmReadRequest")
    data class Read(
        @get:Schema(description = "알람 일련번호", example = "[1, 2]")
        val alarmIdList: List<Long>
    ) : AlarmRequestDto
}