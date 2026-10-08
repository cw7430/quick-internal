package com.quick.module.alarm.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

interface AlarmRequestDto {
    @Schema(name = "AlarmListRequest")
    data class GetList(
        @get:Schema(description = "일련번호", example = "1")
        val alarmId: Long?,
        @get:Schema(description = "등록된 날짜", example = "2026-01-01T00:00:00Z")
        val createdAt: Instant?,
        @get:Schema(description = "페이지 사이즈", example = "10")
        val size: Int = 10
    ) : AlarmRequestDto
}