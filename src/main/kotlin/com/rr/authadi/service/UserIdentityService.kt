package com.rr.authadi.service

import com.github.kittinunf.result.Result
import com.rr.authadi.ServiceRunner
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.exception.UserIdentityAuthenticationException
import com.rr.authadi.service.library.JwtHelper
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserIdentityService {
    @Inject
    lateinit var userIdentityDao: UserIdentityDao

    init {
        ServiceRunner.serviceComponent.inject(this)
    }

    fun addUser(
            userReferenceId:String? = null,
            userKey: String,
            userSecondaryKey: String? = null,
            password: String = JwtHelper.generateUserPassword()
    ) : Result<UUID, Exception> {
        val userInsert = { userIdentityDao.insert(
                    userReferenceId = userReferenceId,
                    userKey = userKey,
                    userSecondaryKey = userSecondaryKey,
                    password = password,
                    clientId = UUID.randomUUID(),
                    secret = JwtHelper.generateUserSecret(),
                    active = true
                    )}
        return Result.of(userInsert)
    }

    fun authenticate(userKey: String, password: String): Result<UserIdentity, Exception> {
        val authenticateUser = {
            val userIdentity = userIdentityDao.authenticatedUser(userKey, password)
            userIdentity ?: throw UserIdentityAuthenticationException("Authentication Failed")
        }
        return Result.of(authenticateUser)
    }
}