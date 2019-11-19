package com.rr.authadi.service.library

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey

object JwtHelper {
    fun generateUserSecret() : String {
        return keyToHex(Keys.secretKeyFor(SignatureAlgorithm.HS512))
    }

    fun getKeyFromSecret(secret: String): SecretKey {
        val secretBytes = secret.toByteArray()
        return Keys.hmacShaKeyFor(secretBytes)
    }
    fun generateUserPassword() : String {
        return keyToHex(Keys.secretKeyFor(SignatureAlgorithm.HS256))
    }

    private fun keyToHex(key: SecretKey): String {
        return key.encoded.joinToString("") {"%02x".format(it)}
    }
}