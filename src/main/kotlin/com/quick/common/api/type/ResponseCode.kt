package com.quick.common.api.type

import org.springframework.http.HttpStatus

enum class ResponseCode(
    val code: String,
    val message: String,
    val status: HttpStatus
) {
    /**
     * 400 Bad Request
     */
    VALIDATION_ERROR("VE", "입력값이 잘못되었습니다.", HttpStatus.BAD_REQUEST),

    /**
     * 401 Unauthorized
     */
    UNAUTHORIZED("UA", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    LOGIN_ERROR("LGE", "아이디 또는 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED),
    PASSWORD_ERROR("PWE", "비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("ET", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("IT", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),

    /**
     * 403 Forbidden
     */
    KEY_ERROR("KE", "접근 키가 잘못되었습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN("FB", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    /**
     * 404 Not Found
     */
    RESOURCE_NOT_FOUND("RNF", "요청한 자원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ENDPOINT_NOT_FOUND("ENF", "요청한 경로가 잘못되었습니다.", HttpStatus.NOT_FOUND),

    /**
     * 409 Conflict
     */
    DUPLICATE_RESOURCE("DR", "이미 존재하는 항목입니다.", HttpStatus.CONFLICT),
    RESOURCE_LIMIT("RL", "항목 갯수 제한을 초과합니다.", HttpStatus.CONFLICT),
    CONFLICT("CF", "요청이 현재 상태와 충돌합니다.", HttpStatus.CONFLICT),

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR("ISE", "서버에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
}