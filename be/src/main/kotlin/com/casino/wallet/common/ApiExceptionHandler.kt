package com.casino.wallet.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

data class ApiError(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now()
)

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException
    ): ResponseEntity<ApiError> {

        return ResponseEntity
            .badRequest()
            .body(
                ApiError(
                    status = HttpStatus.BAD_REQUEST.value(),
                    message = ex.message ?: "Bad request"
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ApiError> {

        val message = ex.bindingResult
            .fieldErrors
            .joinToString(", ") {
                "${it.field}: ${it.defaultMessage}"
            }

        return ResponseEntity
            .badRequest()
            .body(
                ApiError(
                    status = HttpStatus.BAD_REQUEST.value(),
                    message = message
                )
            )
    }
}