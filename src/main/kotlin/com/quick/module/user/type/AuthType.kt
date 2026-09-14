package com.quick.module.user.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import java.util.*

enum class AuthType {
    NATIVE, SOCIAL, CROSS;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): AuthType? {
            if (value.isNullOrBlank()) {
                return null
            }
            try {
                return valueOf(value.uppercase(Locale.getDefault()))
            } catch (e: IllegalArgumentException) {
                e.stackTrace
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "Invalid auth type: $value",
                    "입력 가능값: NATIVE, SOCIAL, CROSS, 입력된 값: $value"
                )
            }
        }
    }
}