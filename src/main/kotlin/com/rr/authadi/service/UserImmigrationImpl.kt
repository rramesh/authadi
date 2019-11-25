package com.rr.authadi.service

import com.rr.authadi.ServiceRunner
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
        val response = userIdentityService.addUser(request)
        responseBuilder.success = response.success
        responseBuilder.message = response.message
        responseBuilder.uuid = response.uuid
        return responseBuilder.build()
    }
}