package com.casino.wallet.payment

import tools.jackson.databind.ObjectMapper
import jakarta.validation.Validator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentCallbackController(
    private val hmacVerifier: HmacVerifier,
    private val objectMapper: ObjectMapper,
    private val validator: Validator,
    private val paymentCallbackService: PaymentCallbackService
) {

    @PostMapping("/callback")
    fun callback(
        @RequestHeader("X-Signature") signature: String,
        @RequestBody rawBody: String
    ): PaymentCallbackResponse {
// проверка за напълно еднакакво боди
        if (!hmacVerifier.isValid(rawBody, signature)) {
            throw InvalidSignatureException()
        }

        val request = objectMapper.readValue(
            rawBody,
            PaymentCallbackRequest::class.java
        )

        val violations = validator.validate(request)

        if (violations.isNotEmpty()) {
            throw IllegalArgumentException(
                violations.joinToString(", ") {
                    it.message
                }
            )
        }

        return paymentCallbackService.process(request)
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidSignatureException :
    RuntimeException("Invalid payment signature")