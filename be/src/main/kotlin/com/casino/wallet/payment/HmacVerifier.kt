package com.casino.wallet.payment

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class HmacVerifier(

    @Value("\${wallet.payment-provider.hmac-secret}")
    private val secret: String
) {

    fun isValid(
        payload: String,
        providedSignature: String
    ): Boolean {

        val mac = Mac.getInstance("HmacSHA256")

        val secretKey = SecretKeySpec(
            secret.toByteArray(StandardCharsets.UTF_8),
            "HmacSHA256"
        )

        mac.init(secretKey)

        val expectedBytes = mac.doFinal(
            payload.toByteArray(StandardCharsets.UTF_8)
        )

        val expectedSignature = expectedBytes
            .joinToString("") { "%02x".format(it) }

        return constantTimeEquals(
            expectedSignature,
            providedSignature
        )
    }

    private fun constantTimeEquals(
        expected: String,
        actual: String
    ): Boolean {

        val expectedBytes =
            expected.toByteArray(StandardCharsets.UTF_8)

        val actualBytes =
            actual.toByteArray(StandardCharsets.UTF_8)

        return java.security.MessageDigest.isEqual(
            expectedBytes,
            actualBytes
        )
    }
}