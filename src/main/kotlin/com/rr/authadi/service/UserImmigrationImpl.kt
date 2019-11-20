package com.rr.authadi.service

import com.rr.authadi.ServiceRunner
import com.rr.authadi.proto.UserImmigrationImplBase
import com.rr.authadi.proto.UserImmigrationRequest
import com.rr.authadi.proto.UserImmigrationResponse
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
                userReferenceId = request.userReferenceId,
                userKey = request.userKey,
                password = request.password,
                userSecondaryKey = request.userSecondaryKey
        )

        result.fold( {value ->
            responseBuilder.success = true
            responseBuilder.message = "User Identity successfully added"
            responseBuilder.uuid = value.toString()
        }, {error ->
            responseBuilder.success = false
            responseBuilder.message = error.message
            responseBuilder.uuid = null
        })
        return responseBuilder.build()
    }
}