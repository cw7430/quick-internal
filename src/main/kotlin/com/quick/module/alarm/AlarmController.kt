package com.quick.module.alarm

import com.quick.common.api.doc.ErrorResponseDoc
import com.quick.module.alarm.dto.request.AlarmRequestDto
import com.quick.module.alarm.dto.response.AlarmResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/alarm")
@Tag(name = "Chat Controller", description = "알람 API")
class AlarmController(
    private val alarmService: AlarmService
) {
    @GetMapping
    @Operation(summary = "알림 불러오기")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "알림 불러오기 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        type = "array",
                        implementation = AlarmResponseDto.ListData::class
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "401", description = "인증오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        oneOf = [
                            ErrorResponseDoc.Unauthorized::class,
                            ErrorResponseDoc.ExpiredToken::class,
                            ErrorResponseDoc.InvalidToken::class
                        ]
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun getAlarmList(@ModelAttribute reqDto: AlarmRequestDto.GetList):
            ResponseEntity<List<AlarmResponseDto.ListData>> =
        ResponseEntity.ok(alarmService.getAlarmList(reqDto))

    @PostMapping
    @Operation(summary = "알림 생성")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "알림 불러오기 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        type = "array",
                        implementation = AlarmResponseDto.ListData::class
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "400", description = "입력값 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.BadRequest::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401", description = "인증오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        oneOf = [
                            ErrorResponseDoc.Unauthorized::class,
                            ErrorResponseDoc.ExpiredToken::class,
                            ErrorResponseDoc.InvalidToken::class
                        ]
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404", description = "없는 자원", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.ResourceNotFound::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun createAlarm(@RequestBody @Valid reqDto: AlarmRequestDto.Alarm): ResponseEntity<Void> {
        alarmService.createAlarm(reqDto)
        return ResponseEntity.noContent().build()
    }
}