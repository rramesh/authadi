package com.rr.authadi.service.library

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import javax.inject.Singleton

@Singleton
class JwtHelper {
    fun generateUserSecret() : String {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512).toString()
    }

    fun generateUserPassword() : String {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256).toString()
    }
}