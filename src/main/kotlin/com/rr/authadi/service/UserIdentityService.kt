package com.rr.authadi.service

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.AuthadiRunner.Companion.logger
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.library.*
import com.rr.proto.authadi.UserImmigrationRequest
import org.postgresql.util.PSQLException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserIdentityService {
    @Inject
    lateinit var userIdentityDao: UserIdentityDao

    data class InsertResponse(
            val success: Boolean,
            val message: String,
            val uuid: String
    )

    init {
        AuthadiRunner.serviceComponent.inject(this)
    }

    fun addUser(request: UserImmigrationRequest): InsertResponse {
        val result = validateInsertRequest(request) then
                ::parseRequest then
                ::validateUserKeyAlreadyPresent then
                ::insertUser
        return when (result) {
            is Success -> {
                successfulInsert(result.value)
            }
            is Failure -> {
                errorInserting(result.errorMessage)
            }
        }
    }

    fun authenticate(userKey: String, password: String): Boolean {
        return userIdentityDao.authenticatedUser(userKey, password) != null
    }

    private data class UserIdentityRequest(
            val userKey: String,
            val userReferenceId: String?,
            val userSecondaryKey: String?,
            val password: String
    )

    private fun parseRequest(request: UserImmigrationRequest): Result<UserIdentityRequest> {
        return Success(UserIdentityRequest(
                userKey = request.userKey,
                userReferenceId = request.userReferenceId.nullIfEmpty(),
                password = request.password,
                userSecondaryKey = request.userSecondaryKey.nullIfEmpty()
        ))
    }

    private fun validateInsertRequest(request: UserImmigrationRequest): Result<UserImmigrationRequest> {
        val userKey = request.userKey.nullIfEmpty()
        val password = request.password.nullIfEmpty()
        return if (userKey == null || password == null)
            Failure("One or more required fields missing. userKey and password mandatory")
        else
            Success(request)
    }

    private fun validateUserKeyAlreadyPresent(uidRequest: UserIdentityRequest): Result<UserIdentityRequest> {
        val userKey = uidRequest.userKey
        val uid = userIdentityDao.findByUserKey(userKey)
        return if (uid == null)
            Success(uidRequest)
        else
            Failure("User Key $userKey already exists")
    }

    private fun insertUser(uidRequest: UserIdentityRequest): Result<UUID> {
        return try {
            val uuid = userIdentityDao.insert(
                    userReferenceId = uidRequest.userReferenceId,
                    userKey = uidRequest.userKey,
                    userSecondaryKey = uidRequest.userSecondaryKey,
                    password = uidRequest.password,
                    clientId = UUID.randomUUID(),
                    secret = JwtHelper.generateUserSecret(),
                    active = true
            )
            Success(uuid)
        } catch (ex: PSQLException) {
            logger.error("Error inserting user identity ${ex.message}")
            ex.printStackTrace()
            Failure("Server Error. Could not insert user identity")
        }
    }

    private fun successfulInsert(uuid: UUID): InsertResponse {
        return InsertResponse(
                success = true,
                message = "User Identity successfully added",
                uuid = uuid.toString()
        )
    }

    private fun errorInserting(message: String): InsertResponse {
        return InsertResponse(
                success = false,
                message = message,
                uuid = ""
        )
    }
}