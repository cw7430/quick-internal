package com.quick.module.user

import com.quick.common.api.doc.ErrorResponseDoc
import com.quick.module.user.dto.request.*
import com.quick.module.user.dto.response.LoginResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller", description = "계정 API")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/login/native")
    @Operation(summary = "등록된 회원 로그인")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "로그인 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LoginResponseDto::class)
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
            responseCode = "401", description = "잘못 된 계정정보", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.LoginError::class)
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
    fun nativeLogin(@RequestBody @Valid reqDto: NativeLoginRequestDto): ResponseEntity<LoginResponseDto> =
        ResponseEntity.ok(userService.nativeLogin(reqDto))


    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급")
    @SecurityRequirement(name = "refresh-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "재발급 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LoginResponseDto::class)
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
    fun refresh(req: HttpServletRequest, @RequestBody reqDto: RefreshRequestDto): ResponseEntity<LoginResponseDto> =
        ResponseEntity.ok(userService.refresh(req, reqDto))

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "로그아웃 성공"
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
    fun logout(@RequestBody reqDto: LogoutRequestDto): ResponseEntity<Void> {
        userService.logout(reqDto)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/email")
    @Operation(summary = "Email 중복 체크")
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "Email 중복 체크 성공"
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
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "409", description = "중복된 Email", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.DuplicateResource::class)
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
    fun checkEmail(@RequestBody @Valid reqDto: CreateNativeUserRequestDto.CheckEmail): ResponseEntity<Void> {
        userService.checkEmail(reqDto)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/register")
    @Operation(summary = "회원 가입")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "회원 가입 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LoginResponseDto::class)
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
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "409", description = "중복된 Email", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.DuplicateResource::class)
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
    fun createNativeUsers(@RequestBody @Valid reqDto: CreateNativeUserRequestDto.Create)
            : ResponseEntity<LoginResponseDto> = ResponseEntity.ok(userService.createNativeUsers(reqDto))


    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경")
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "비밀번호 변경 성공"
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
                            ErrorResponseDoc.InvalidToken::class,
                            ErrorResponseDoc.PasswordError::class
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
            responseCode = "409", description = "중복된 비밀번호", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.DuplicateResource::class)
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
    fun updatePassword(@RequestBody @Valid reqDto: UpdateNativeUserRequestDto.Password): ResponseEntity<Void> {
        userService.updatePassword(reqDto)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 변경")
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "닉네임 변경 성공"
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
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun updateNickName(@RequestBody @Valid reqDto: UpdateNativeUserRequestDto.NickName): ResponseEntity<Void> {
        userService.updateNickName(reqDto)
        return ResponseEntity.noContent().build()
    }
}