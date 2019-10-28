package com.rr.authadi.dao

import com.rr.authadi.ServiceRunner
import com.rr.authadi.entities.vault.User
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDao {
    @Inject lateinit var dataHandle :KotlinEntityDataStore<Any>

    init {
        ServiceRunner.serviceComponent.inject(this)
    }

    fun findUserByPhoneNumber(phoneNumber: String): User {
        val result= dataHandle.select(User::class)
                .where(User::phone_number eq phoneNumber)
                .get()
        return result.first()
    }
}