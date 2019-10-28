package com.rr.authadi.dao

import com.rr.authadi.entities.vault.User
import io.requery.kotlin.eq
import javax.inject.Singleton

@Singleton
class UserDao : AbstractDao() {
    fun findUserByPhoneNumber(phoneNumber: String): User? {
        val result = data.select(User::class)
                .where(User::phone_number eq phoneNumber)
                .get()
        return result.firstOrNull()
    }
}