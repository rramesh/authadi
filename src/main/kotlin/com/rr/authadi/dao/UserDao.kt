package com.rr.authadi.dao

import com.rr.authadi.entities.vault.User
import org.jdbi.v3.sqlobject.SqlObject
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface UserDao : SqlObject {
    @SqlQuery("SELECT * FROM USERS WHERE phone_number = :phone_number LIMIT 1")
    fun findByPhoneNumber(@Bind("phone_number") phoneNumber: String): User?

    @SqlUpdate("INSERT INTO USERS(user_uuid, phone_number) VALUES(:user_uuid, :phone_number)")
    fun addWithDefaults(
            @Bind("user_uuid") userUuid: String,
            @Bind("phone_number") phoneNumber: String
    ): Boolean

    @SqlUpdate(""" INSERT INTO USERS
                    (user_uuid, phone_number, password, client_id, secret) 
                    VALUES
                    (:user_uuid, :phone_number, :password, :client_id, :secret) """)
    fun add(
            @Bind("user_uuid") userUuid: String,
            @Bind("phone_number") phoneNumber: String,
            @Bind("password") password: String,
            @Bind("client_id") clientId: String,
            @Bind("secret") secret: String
    ): Boolean

    @SqlQuery("SELECT * FROM USERS WHERE phone_number = :phone_number and password = :password LIMIT 1")
    fun authenticate(
            @Bind("phone_number") phoneNumber: String,
            @Bind("password") password: String
    ) : User?
}
