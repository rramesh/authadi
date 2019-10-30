package com.rr.authadi.entities.vault

import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.time.LocalDateTime

data class User constructor(
        val uuid: String? = null,
        @ColumnName("phone_number") val phoneNumber: String,
        @ColumnName("user_uuid") val userUuid: String,
        val password: String? = null,
        @ColumnName("client_id") val clientId: String? = null,
        val secret: String? = null,
        @ColumnName("active") val isActive: Boolean? = true,
        @ColumnName("created_at")  val createdAt: LocalDateTime? = null,
        @ColumnName("updated_at")  val updatedAt: LocalDateTime? = null
        )
