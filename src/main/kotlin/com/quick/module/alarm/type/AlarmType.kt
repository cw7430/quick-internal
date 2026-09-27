package com.quick.module.alarm.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import java.util.*

enum class AlarmType {
    CREATE, ACCEPT, MESSAGE;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): AlarmType? {
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
                    "입력 가능값: CREATE, ACCEPT, MESSAGE, 입력된 값: $value"
                )
            }
        }
    }
}