package com.rr.authadi.entities.vault

import com.rr.authadi.library.JwtHelper
import io.jsonwebtoken.Jwts
import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.sql.Timestamp
import java.util.*
import javax.crypto.SecretKey

data class UserIdentity constructor(
        @ColumnName("uuid") val uuid: UUID,
        @ColumnName("user_reference_id") val userReferenceId: String? = null,
        @ColumnName("user_key") val userKey: String,
        @ColumnName("user_secondary_key") val userSecondaryKey: String? = null,
        @ColumnName("password") val password: String,
        @ColumnName("client_id") val clientId: UUID,
        @ColumnName("secret") val secret: String,
        @ColumnName("active") val isActive: Boolean? = true,
        @ColumnName("created_at")  val createdAt: Timestamp,
        @ColumnName("updated_at")  val updatedAt: Timestamp
) {
        fun getJws() : String {
                val key: SecretKey = JwtHelper.getKeyFromSecret(this.secret)
                return Jwts.builder()
                        .setSubject(this.uuid.toString())
                        .signWith(key)
                        .compact()

        }
}
