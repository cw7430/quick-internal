package com.quick.common.api.response

import com.quick.common.api.type.ResponseCode

sealed class ErrorResponseDto {
    abstract val code: String
    abstract val message: String

    private data class Simple(
        override val code: String,
        override val message: String
    ) : ErrorResponseDto()

    private data class WithErrors<T>(
        override val code: String,
        override val message: String,
        val errors: T
    ) : ErrorResponseDto()

    companion object {
        fun from(responseCode: ResponseCode): ErrorResponseDto =
            Simple(responseCode.code, responseCode.message)

        fun <T> of(
            responseCode: ResponseCode,
            errors: T
        ): ErrorResponseDto =
            WithErrors(responseCode.code, responseCode.message, errors)
    }
}