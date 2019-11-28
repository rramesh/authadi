package com.rr.authadi.service

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.library.*
import com.rr.proto.authadi.PasswordAuthenticationRequest
import com.rr.proto.authadi.TokenType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAuthenticationService {
    @Inject
    lateinit var userIdentityDao: UserIdentityDao

    data class AuthenticationResponse(
            val success: Boolean,
            val message: String,
            val uuid: String,
            val uRefId: String,
            val tokenType: TokenType,
            val token: String
    )

    init {
        AuthadiRunner.serviceComponent.inject(this)
    }

    fun passwordAuthenticate(request: PasswordAuthenticationRequest): AuthenticationResponse {
        val result = validatePasswordAuthenticateRequest(request) then
                ::parseRequest then
                ::passwordAuthenticateUser
        return when (result) {
            is Success -> {
                AuthenticationResponse(
                        success = true,
                        message = "Successfully Authenticated",
                        uuid = result.value.uuid.toString(),
                        uRefId = result.value.userReferenceId ?: "",
                        tokenType = TokenType.BEARER,
                        token = result.value.getJws()
                )
            }
            is Failure -> {
                AuthenticationResponse(
                        success = false,
                        message = result.errorMessage,
                        uuid = "",
                        uRefId = "",
                        tokenType = TokenType.BEARER,
                        token = ""
                )
            }
        }

    }

    private fun validatePasswordAuthenticateRequest(request: PasswordAuthenticationRequest): Result<PasswordAuthenticationRequest> {
        val userKey = request.userKey.nullIfEmpty()
        return if (userKey == null)
            Failure("Missing userKey. userKey is mandatory")
        else
            Success(request)
    }

    private fun parseRequest(request: PasswordAuthenticationRequest): Result<Pair<String, String>> {
        return Success(Pair<String, String>(request.userKey, request.password))
    }

    private fun passwordAuthenticateUser(request: Pair<String, String>): Result<UserIdentity> {
        val uId = userIdentityDao.authenticatedUser(request.first, request.second)
        return if (uId != null) {
            Success(uId)
        } else {
            Failure("Authentication Failed")
        }
    }
}