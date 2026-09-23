package com.quick.module.chat

import com.quick.common.api.doc.ErrorResponseDoc
import com.quick.module.chat.dto.request.ChatMessageRequestDto
import com.quick.module.chat.dto.request.ChatRoomRequestDto
import com.quick.module.chat.dto.response.ChatMessageResponseDto
import com.quick.module.chat.dto.response.ChatRoomResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat Controller", description = "채팅 API")
class ChatController(
    private val chatService: ChatService
) {
    @GetMapping("/room")
    @Operation(summary = "채팅방 목록 불러오기")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "채팅방 불러오기 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        type = "array",
                        implementation = ChatRoomResponseDto.ListData::class
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
    fun getChatRoomList(@ModelAttribute reqDto: ChatRoomRequestDto):
            ResponseEntity<List<ChatRoomResponseDto.ListData>> =
        ResponseEntity.ok(chatService.getChatRoomList(reqDto))

    @GetMapping("/room/{chatRoomId}")
    @Operation(summary = "채팅방 상세보기 불러오기")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "채팅방 불러오기 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        implementation = ChatRoomResponseDto.DetailData::class
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
            responseCode = "404", description = "없는 요소", content = [
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
    fun getChatRoom(@PathVariable chatRoomId: Long): ResponseEntity<ChatRoomResponseDto.DetailData> =
        ResponseEntity.ok(chatService.getChatRoom(chatRoomId))

    @GetMapping("/message/{chatRoomId}")
    @Operation(summary = "채팅 메세지 목록 불러오기")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "채팅 메세지 불러오기 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        type = "array",
                        implementation = ChatMessageResponseDto::class
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
    fun getMessageList(@PathVariable chatRoomId: Long, @ModelAttribute reqDto: ChatMessageRequestDto.GetList):
            ResponseEntity<List<ChatMessageResponseDto>> =
        ResponseEntity.ok(chatService.getMessageList(chatRoomId, reqDto))

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