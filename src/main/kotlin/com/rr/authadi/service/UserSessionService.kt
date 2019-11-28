package com.rr.authadi.service

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.library.Failure
import com.rr.authadi.library.Result
import com.rr.authadi.library.Success
import com.rr.authadi.library.then
import com.rr.proto.authadi.TokenType
import com.rr.proto.authadi.UserSessionRequest
import com.rr.proto.authadi.UserSessionRequest.IdCase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionService {
    @Inject
    lateinit var userIdentityDao: UserIdentityDao

    private enum class FindBy {
        USER_KEY, USER_REF_ID
    }

    private data class SessionValidationRequest(
            var uuid: UUID? = null,
            var uRefId: String? = null,
            var tokenType: TokenType = TokenType.BEARER,
            var token: String? = null,
            var findBy: FindBy = FindBy.USER_KEY,
            var userIdentity: UserIdentity? = null
    )

    data class SessionValidationResponse(
            val success: Boolean,
            val message: String
    )

    init {
        AuthadiRunner.serviceComponent.inject(this)
    }

    fun isValidSession(request: UserSessionRequest): SessionValidationResponse {
        val result = validateSessionRequest(request) then
                ::validUserIdentity then
                ::validateJws
        return when (result) {
            is Success -> {
                SessionValidationResponse(
                        success = true,
                        message = "Valid Token"
                )
            }
            is Failure -> {
                SessionValidationResponse(
                        success = false,
                        message = result.errorMessage
                )
            }
        }
    }

    private fun validateJws(pair: Pair<UserIdentity, String>): Result<Boolean> {
        val (userIdentity, token) = pair
        return if (userIdentity.validateJws(token)) {
            Success(true)
        } else {
            Failure("Token validation failed")
        }
    }

    private fun validUserIdentity(request: SessionValidationRequest): Result<Pair<UserIdentity, String>> {
        val userIdentity = when (request.findBy) {
            FindBy.USER_KEY ->
                request.uuid?.let { userIdentityDao.findByUuid(it) }
            FindBy.USER_REF_ID ->
                request.uRefId?.let { userIdentityDao.findByUserByReferenceId(it) }
        }
        return if (userIdentity == null) {
            Failure("Could not find user identity for the given request")
        } else {
            Success(Pair(userIdentity, request.token!!))
        }
    }

    private fun validateSessionRequest(request: UserSessionRequest): Result<SessionValidationRequest> {
        val parsedRequest = SessionValidationRequest()
        val result = validateOneOfKeys(
                Pair(request, parsedRequest)
        ) then ::validateTokenType then ::validateToken
        return when (result) {
            is Success -> Success(result.value.second)
            is Failure -> Failure(result.errorMessage)
        }
    }

    private fun validateOneOfKeys(
            pairedRequest: Pair<UserSessionRequest, SessionValidationRequest>
    ): Result<Pair<UserSessionRequest, SessionValidationRequest>> {
        val (request, parsedRequest) = pairedRequest
        val id = request.idCase
        when (id) {
            null, IdCase.ID_NOT_SET -> {
                return Failure("Either userKey or uRefId has to be set")
            }
            IdCase.UUID -> {
                parsedRequest.uuid = UUID.fromString(request.uuid)
            }
            IdCase.UREFID -> {
                parsedRequest.uRefId = request.uRefId
                parsedRequest.findBy = FindBy.USER_REF_ID
            }
        }
        return Success(Pair(request, parsedRequest))
    }

    private fun validateTokenType(
            pairedRequest: Pair<UserSessionRequest, SessionValidationRequest>
    ): Result<Pair<UserSessionRequest, SessionValidationRequest>> {
        val (request, parsedRequest) = pairedRequest
        parsedRequest.tokenType = request.tokenType
        if (parsedRequest.tokenType == TokenType.UNRECOGNIZED) {
            return Failure("Invalid tokenType, should be one of ${TokenType.values().joinToString()}")
        }
        return Success(Pair(request, parsedRequest))
    }

    private fun validateToken(
            pairedRequest: Pair<UserSessionRequest, SessionValidationRequest>
    ): Result<Pair<UserSessionRequest, SessionValidationRequest>> {
        val (request, parsedRequest) = pairedRequest
        parsedRequest.token = request.token
        if (parsedRequest.token.isNullOrBlank()) {
            return Failure("Field token cannot be blank")
        }
        return Success(Pair(request, parsedRequest))
    }
}