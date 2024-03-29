package com.rr.authadi.controller

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.service.UserIdentityService
import com.rr.proto.authadi.UserImmigrationImplBase
import com.rr.proto.authadi.UserImmigrationRequest
import com.rr.proto.authadi.UserImmigrationResponse
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors.newFixedThreadPool
import javax.inject.Inject

class UserImmigrationController : UserImmigrationImplBase(
        coroutineContext = newFixedThreadPool(4).asCoroutineDispatcher()
) {
    @Inject
    lateinit var userIdentityService: UserIdentityService

    init {
        AuthadiRunner.serviceComponent.inject(this)
    }

    override suspend fun addUserIdentity(request: UserImmigrationRequest): UserImmigrationResponse {
        val response = userIdentityService.addUser(request)
        return UserImmigrationResponse.newBuilder()
                .setSuccess(response.success)
                .setMessage(response.message)
                .setUuid(response.uuid)
                .build()
    }
}