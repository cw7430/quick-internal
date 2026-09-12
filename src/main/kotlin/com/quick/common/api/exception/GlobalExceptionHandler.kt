package com.quick.common.api.exception

import com.quick.common.api.response.ErrorResponseDto
import com.quick.common.api.response.ValidationError
import com.quick.common.api.type.ResponseCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataAccessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponseDto> {

        val errors = ex.bindingResult.fieldErrors.map {
            ValidationError(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value"
            )
        }

        log.warn { "Validation failed: $errors" }

        return ResponseEntity(
            ErrorResponseDto.of(
                ResponseCode.VALIDATION_ERROR,
                errors
            ),
            ResponseCode.VALIDATION_ERROR.status
        )
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException
    ): ResponseEntity<ErrorResponseDto> {
        log.error { "Custom exception occurred: ${ex.responseCode.message}" }

        if (ex.fieldName != null && ex.customMessage != null) {
            val errors = listOf(
                ValidationError(
                    field = ex.fieldName,
                    message = ex.customMessage
                )
            )

            return ResponseEntity(
                ErrorResponseDto.of(
                    ex.responseCode,
                    errors
                ),
                ex.responseCode.status
            )
        }

        return ResponseEntity(
            ErrorResponseDto.from(ex.responseCode),
            ex.responseCode.status
        )
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDatabaseException(ex: DataAccessException): ResponseEntity<ErrorResponseDto> {
        log.error(ex) { "Database exception occurred" }

        return ResponseEntity(
            ErrorResponseDto.from(ResponseCode.INTERNAL_SERVER_ERROR),
            ResponseCode.INTERNAL_SERVER_ERROR.status
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponseDto> {
        log.error(ex) { "Unhandled exception occurred" }

        return ResponseEntity(
            ErrorResponseDto.from(ResponseCode.INTERNAL_SERVER_ERROR),
            ResponseCode.INTERNAL_SERVER_ERROR.status
        )
    }
}