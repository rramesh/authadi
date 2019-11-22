package com.rr.authadi.service

import com.rr.authadi.ServiceRunner
import com.rr.authadi.ServiceRunner.Companion.logger
import com.rr.authadi.setup.nullIfEmpty
import com.rr.proto.authadi.UserImmigrationImplBase
import com.rr.proto.authadi.UserImmigrationRequest
import com.rr.proto.authadi.UserImmigrationResponse
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors.newFixedThreadPool
import javax.inject.Inject

class UserImmigrationImpl : UserImmigrationImplBase(
        coroutineContext = newFixedThreadPool(4).asCoroutineDispatcher()
){
    @Inject
    lateinit var userIdentityService: UserIdentityService

    init {
        ServiceRunner.serviceComponent.inject(this)
    }
    override suspend fun addUserIdentity(request: UserImmigrationRequest): UserImmigrationResponse {
        val responseBuilder = UserImmigrationResponse.newBuilder()
        val result =userIdentityService.addUser(
                userReferenceId = request.userReferenceId.nullIfEmpty(),
                userKey = request.userKey,
                password = request.password,
                userSecondaryKey = request.userSecondaryKey.nullIfEmpty()
        )

        result.fold( {value ->
            responseBuilder.success = true
            responseBuilder.message = "User Identity successfully added"
            responseBuilder.uuid = value.toString()
        }, {error ->
            logger.error("Error inserting user identity - ${error.message}")
            responseBuilder.success = false
            responseBuilder.message = error.message
            responseBuilder.uuid = ""
        })
        return responseBuilder.build()
    }
}