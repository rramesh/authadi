package com.rr.authadi.controller

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.service.UserAuthenticationService
import com.rr.authadi.service.UserSessionService
import com.rr.proto.authadi.*
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Inject

class UserAuthenticationController : UserAuthenticationImplBase(
        coroutineContext = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
) {
    @Inject
    lateinit var userAuthenticationService: UserAuthenticationService

    @Inject
    lateinit var userSessionService: UserSessionService

    init {
        AuthadiRunner.serviceComponent.inject(this)
    }

    override suspend fun passwordAuthenticate(request: PasswordAuthenticationRequest): PasswordAuthenticationResponse {
        val response = userAuthenticationService.passwordAuthenticate(request)
        return PasswordAuthenticationResponse.newBuilder()
                .setSuccess(response.success)
                .setMessage(response.message)
                .setUuid(response.uuid)
                .setURefId(response.uRefId)
                .setBearerToken(response.token)
                .build()
    }

    override suspend fun validateUserSession(request: UserSessionRequest): UserSessionResponse {
        val response = userSessionService.isValidSession(request)
        return UserSessionResponse.newBuilder()
                .setSuccess(response.success)
                .setMessage(response.message)
                .build()
    }
}