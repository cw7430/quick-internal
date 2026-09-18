package com.quick.common.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import java.util.Locale.getDefault

enum class YN {
    Y, N;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): YN? {
            if (value.isNullOrBlank()) {
                return null
            }
            try {
                return YN.valueOf(value.uppercase(getDefault()))
            } catch (e: IllegalArgumentException) {
                e.stackTrace
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "yn",
                    "입력 가능값: Y, N, 입력된 값: $value"
                )
            }
        }
    }
}