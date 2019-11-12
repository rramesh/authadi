package com.rr.authadi.service

import com.rr.authadi.ServiceRunner.Companion.logger
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.service.library.JwtHelper
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService {
    @Inject
    lateinit var jwtHelper: JwtHelper

    @Inject
    lateinit var userIdentityDao: UserIdentityDao

    fun addUser(
            userReferenceId:String? = null,
            userKey: String,
            userSecondaryKey: String? = null,
            password: String = jwtHelper.generateUserPassword()
    ) : Pair<Boolean, UUID?> {
        return try {
            val uuid = userIdentityDao.insert(
                    userReferenceId = userReferenceId,
                    userKey = userKey,
                    userSecondaryKey = userSecondaryKey,
                    password = password,
                    clientId = UUID.randomUUID(),
                    secret = jwtHelper.generateUserSecret(),
                    active = true
            )
            Pair(true, uuid)
        } catch(ute: UnableToExecuteStatementException) {
            logger.error("Error while inserting user identity")
            ute.printStackTrace()
            Pair(false, null)
        }
    }

    fun authenticate(userKey: String, password: String): Pair<Boolean, UUID?> {
        val uuid = userIdentityDao.authenticatedUser(userKey, password)
        return Pair(uuid != null, uuid)
    }
}