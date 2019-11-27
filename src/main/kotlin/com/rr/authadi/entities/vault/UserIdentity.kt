package com.rr.authadi.entities.vault

import com.rr.authadi.AuthadiRunner.Companion.logger
import com.rr.authadi.library.JwtHelper
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.sql.Timestamp
import java.util.*

data class UserIdentity constructor(
        @ColumnName("uuid") val uuid: UUID,
        @ColumnName("user_reference_id") val userReferenceId: String? = null,
        @ColumnName("user_key") val userKey: String,
        @ColumnName("user_secondary_key") val userSecondaryKey: String? = null,
        @ColumnName("password") val password: String,
        @ColumnName("client_id") val clientId: UUID,
        @ColumnName("secret") val secret: String,
        @ColumnName("active") val isActive: Boolean? = true,
        @ColumnName("created_at") val createdAt: Timestamp,
        @ColumnName("updated_at") val updatedAt: Timestamp
) {
    fun getJws(): String {
        val key = JwtHelper.getKeyFromSecret(this.secret)
        val uuid = this.uuid.toString()
        val uRefId = this.userReferenceId ?: ""
        // Default to 1 hour expiration
        val expiryDate = Date(System.currentTimeMillis() + 3600 * 1000)
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("authadi")
                .setSubject(uuid)
                .setExpiration(expiryDate)
                .setIssuedAt(Date())
                .setHeaderParam("kid", uuid)
                .setHeaderParam("userReferenceId", uRefId)
                .signWith(key)
                .compact()
    }

    fun validateJws(token: String): Boolean {
        val key = JwtHelper.getKeyFromSecret(this.secret)
        return try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token)
            true
        } catch (ex: JwtException) {
            logger.warn("JWT token validation failed with exception - ${ex.message}")
            false
        }
    }
}
