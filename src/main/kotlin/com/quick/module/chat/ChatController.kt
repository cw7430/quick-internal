package com.quick.module.chat

import com.quick.common.api.doc.ErrorResponseDoc
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat Controller", description = "채팅 API")
class ChatController(
    private val chatService: ChatService
) {
    @PostMapping("/room/{userId}")
    @Operation(summary = "채팅방 생성")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "채팅방 생성 성공"
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
            responseCode = "409", description = "중복된 채팅방", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        oneOf = [
                            ErrorResponseDoc.Conflict::class,
                            ErrorResponseDoc.DuplicateResource::class
                        ]
                    )
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
    fun createChatRoom(@PathVariable userId: Long): ResponseEntity<Void> {
        chatService.createChatRoom(userId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/room/{chatMemberId}")
    @Operation(summary = "채팅방 수락")
    @SecurityRequirement(
        name = "access-token"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "채팅방 수락 성공"
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
    fun acceptChatRoom(@PathVariable chatMemberId: Long): ResponseEntity<Void> {
        chatService.acceptChatRoom(chatMemberId)
        return ResponseEntity.noContent().build()
    }
}