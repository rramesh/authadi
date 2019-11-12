package com.rr.authadi.entities.vault

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
        @ColumnName("created_at")  val createdAt: Timestamp,
        @ColumnName("updated_at")  val updatedAt: Timestamp
        )
