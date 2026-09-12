package com.quick.common.api.doc

import com.quick.common.api.response.ValidationError
import com.quick.common.api.type.ResponseCode
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "ErrorResponse")
class ErrorResponseDoc {
    /** 400 Bad Request - Validation Error */
    class BadRequest {
        @get:Schema(example = "VE")
        val code: String = ResponseCode.VALIDATION_ERROR.code

        @get:Schema(example = "입력값이 잘못되었습니다.")
        val message: String = ResponseCode.VALIDATION_ERROR.message

        @get:Schema(description = "에러내용")
        val validationErrors: List<ValidationError>? = null
    }

    /** 401 Unauthorized */
    class Unauthorized {
        @get:Schema(example = "UA")
        val code: String = ResponseCode.UNAUTHORIZED.code

        @get:Schema(example = "로그인이 필요합니다.")
        val message: String = ResponseCode.UNAUTHORIZED.message
    }

    /** 401 Unauthorized - Login Error */
    class LoginError {
        @get:Schema(example = "LGE")
        val code: String = ResponseCode.LOGIN_ERROR.code

        @get:Schema(example = "아이디 또는 비밀번호가 잘못되었습니다.")
        val message: String = ResponseCode.LOGIN_ERROR.message
    }

    /** 401 Unauthorized - Password Error */
    class PasswordError {
        @get:Schema(example = "PWE")
        val code: String = ResponseCode.PASSWORD_ERROR.code

        @get:Schema(example = "비빌번호가 잘못되었습니다.")
        val message: String = ResponseCode.PASSWORD_ERROR.message
    }

    /**
     * 401 Unauthorized - Expired Token
     */
    class ExpiredToken {
        @get:Schema(example = "ET")
        val code: String = ResponseCode.EXPIRED_TOKEN.code

        @get:Schema(example = "토큰이 만료되었습니다.")
        val message: String = ResponseCode.EXPIRED_TOKEN.message
    }

    /**
     * 401 Unauthorized - Invalid Token
     */
    class InvalidToken {
        @get:Schema(example = "IT")
        val code: String = ResponseCode.INVALID_TOKEN.code

        @get:Schema(example = "토큰이 잘못되었습니다.")
        val message: String = ResponseCode.INVALID_TOKEN.message
    }

    /** 403 Forbidden */
    class Forbidden {
        @get:Schema(example = "FB")
        val code: String = ResponseCode.FORBIDDEN.code

        @get:Schema(example = "접근 권한이 없습니다.")
        val message: String = ResponseCode.FORBIDDEN.message
    }

    /** 403 Forbidden - Key Error */
    class KeyError {
        @get:Schema(example = "KE")
        val code: String = ResponseCode.KEY_ERROR.code

        @get:Schema(example = "요청한 키가 잘못되었습니다.")
        val message: String = ResponseCode.KEY_ERROR.message
    }

    /** 404 Not Found */
    class ResourceNotFound {
        @get:Schema(example = "RNF")
        val code: String = ResponseCode.RESOURCE_NOT_FOUND.code

        @get:Schema(example = "요청한 리소스를 찾을 수 없습니다.")
        val message: String = ResponseCode.RESOURCE_NOT_FOUND.message
    }

    /** 404 Not Found */
    class EndpointNotFound {
        @get:Schema(example = "ENF")
        val code: String = ResponseCode.ENDPOINT_NOT_FOUND.code

        @get:Schema(example = "요청한 경로가 잘못되었습니다.")
        val message: String = ResponseCode.ENDPOINT_NOT_FOUND.message
    }

    /** 409 Conflict */
    class DuplicateResource {
        @get:Schema(example = "DR")
        val code: String = ResponseCode.DUPLICATE_RESOURCE.code

        @get:Schema(example = "이미 존재하는 항목입니다.")
        val message: String = ResponseCode.DUPLICATE_RESOURCE.message
    }

    /** 409 Conflict */
    class Conflict {
        @get:Schema(example = "CF")
        val code: String = ResponseCode.CONFLICT.code

        @get:Schema(example = "요청이 현재 상태와 충돌합니다.")
        val message: String = ResponseCode.CONFLICT.message
    }

    /** 500 Internal Server Error */
    class InternalServerError {
        @get:Schema(example = "ISE")
        val code: String = ResponseCode.INTERNAL_SERVER_ERROR.code

        @get:Schema(example = "서버에서 문제가 발생했습니다.")
        val message: String = ResponseCode.INTERNAL_SERVER_ERROR.message
    }
}