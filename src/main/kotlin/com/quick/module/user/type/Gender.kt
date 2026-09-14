package com.quick.module.user.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import java.util.Locale.getDefault

enum class Gender {
    M, F;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): Gender? {
            if (value.isNullOrBlank()) {
                return null
            }
            try {
                return Gender.valueOf(value.uppercase(getDefault()))
            } catch(e: IllegalArgumentException) {
                e.stackTrace
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "gender",
                    "입력 가능값: M, F, 입력된 값: $value"
                )
            }

        }
    }


}