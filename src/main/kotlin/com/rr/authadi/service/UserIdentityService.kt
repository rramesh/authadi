package com.rr.authadi.service

import com.github.kittinunf.result.NoException
import com.github.kittinunf.result.Result
import com.rr.authadi.ServiceRunner.Companion.logger
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.exception.UserIdentityAuthenticationException
import com.rr.authadi.service.library.JwtHelper
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.*
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserIdentityService {
    @Inject
    lateinit var userIdentityDao: UserIdentityDao

    fun addUser(
            userReferenceId:String? = null,
            userKey: String,
            userSecondaryKey: String? = null,
            password: String = JwtHelper.generateUserPassword()
    ) : Result<UUID, UnableToExecuteStatementException> {
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

    fun authenticate(userKey: String, password: String): Result<UserIdentity, UserIdentityAuthenticationException> {
        val authenticateUser = {
            val userIdentity = userIdentityDao.authenticatedUser(userKey, password)
            userIdentity ?: throw UserIdentityAuthenticationException("Authentication Failed")
        }
        return Result.of(authenticateUser)
    }
}