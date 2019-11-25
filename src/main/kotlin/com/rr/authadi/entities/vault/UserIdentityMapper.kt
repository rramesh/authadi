package com.rr.authadi.entities.vault

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.*

class UserIdentityMapper : RowMapper<UserIdentity> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): UserIdentity? {
        val userIdentity = rs?.let {
            UserIdentity(
                    UUID.fromString(it.getString("uuid")),
                    it.getString("user_reference_id"),
                    it.getString("user_key"),
                    it.getString("user_secondary_key"),
                    it.getString("password"),
                    UUID.fromString(it.getString("client_id")),
                    it.getString("secret"),
                    it.getBoolean("active"),
                    it.getTimestamp("created_at"),
                    it.getTimestamp("updated_at")
            )
        }
        return userIdentity
    }
}