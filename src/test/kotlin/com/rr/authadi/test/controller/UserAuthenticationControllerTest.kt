package com.rr.authadi.test.controller

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.controller.UserAuthenticationController
import com.rr.authadi.service.UserAuthenticationService
import com.rr.authadi.service.UserSessionService
import com.rr.proto.authadi.PasswordAuthenticationRequest
import com.rr.proto.authadi.TokenType
import com.rr.proto.authadi.UserAuthenticationGrpc
import com.rr.proto.authadi.UserSessionRequest
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Rule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthenticationControllerTest {
    @MockK
    private lateinit var userAuthenticationService: UserAuthenticationService

    @MockK
    private lateinit var userSessionService: UserSessionService

    @InjectMockKs
    private lateinit var userAuthenticationController: UserAuthenticationController

    @Rule
    private val grpcCleanup = GrpcCleanupRule()
    private val serverName = InProcessServerBuilder.generateName()
    private var server: Server
    private var channel: ManagedChannel

    init {
        mockkObject(AuthadiRunner)
        every { AuthadiRunner.serviceComponent.inject(any() as UserAuthenticationController) } just runs
        MockKAnnotations.init(this, relaxUnitFun = true)
        server = grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor()
                .addService(userAuthenticationController).build().start())
        channel = grpcCleanup.register(InProcessChannelBuilder
                .forName(serverName).directExecutor().build())
    }

    @Test
    fun `it should successfully authenticate user and return token`() {
        val blockingStub = UserAuthenticationGrpc.newBlockingStub(channel)
        val requestBuilder = PasswordAuthenticationRequest.newBuilder()
        requestBuilder.setUserKey("johndoe@johnydoe.com")
        requestBuilder.setPassword("Hush Hush Password")
        val request = requestBuilder.build()
        val uisResponse = UserAuthenticationService.AuthenticationResponse(
                success = true,
                message = "Successfully Authenticated",
                uuid = "UserUUID",
                uRefId = "UserRefId",
                tokenType = TokenType.BEARER,
                token = "JWS Token"
        )
        every { userAuthenticationService.passwordAuthenticate(request) } returns uisResponse

        val response = blockingStub.passwordAuthenticate(request)

        assertTrue(response.success)
        assertEquals("Successfully Authenticated", response.message)
        assertEquals("UserUUID", response.uuid)
        assertEquals("UserRefId", response.uRefId)
        assertEquals(TokenType.BEARER, response.tokenType)
        assertEquals("JWS Token", response.token)
    }

    @Test
    fun `it should fail authenticating user and return false`() {
        val blockingStub = UserAuthenticationGrpc.newBlockingStub(channel)
        val requestBuilder = PasswordAuthenticationRequest.newBuilder()
        requestBuilder.setUserKey("johndoe@johnydoe.com")
        requestBuilder.setPassword("Hush Hush Password")
        val request = requestBuilder.build()
        val uisResponse = UserAuthenticationService.AuthenticationResponse(
                success = false,
                message = "Authentication Failed",
                uuid = "",
                uRefId = "",
                tokenType = TokenType.BEARER,
                token = ""
        )
        every { userAuthenticationService.passwordAuthenticate(request) } returns uisResponse

        val response = blockingStub.passwordAuthenticate(request)

        assertFalse(response.success)
        assertEquals("Authentication Failed", response.message)
        assertEquals("", response.uuid)
        assertEquals("", response.uRefId)
        assertEquals(TokenType.BEARER, response.tokenType)
        assertEquals("", response.token)
    }

    @Test
    fun `it should successfully validate user session`() {
        val blockingStub = UserAuthenticationGrpc.newBlockingStub(channel)
        val requestBuilder = UserSessionRequest.newBuilder()
        requestBuilder.setUuid(UUID.randomUUID().toString())
        requestBuilder.setTokenType(TokenType.BEARER)
        requestBuilder.setToken("JWS Token")
        val request = requestBuilder.build()
        val uisResponse = UserSessionService.SessionValidationResponse(
                success = true,
                message = "Valid Token"
        )
        every { userSessionService.isValidSession(request) } returns uisResponse

        val response = blockingStub.validateUserSession(request)

        assertTrue(response.success)
        assertEquals("Valid Token", response.message)
    }

    @Test
    fun `it should return false with invalid user session`() {
        val blockingStub = UserAuthenticationGrpc.newBlockingStub(channel)
        val requestBuilder = UserSessionRequest.newBuilder()
        requestBuilder.setUuid(UUID.randomUUID().toString())
        requestBuilder.setTokenType(TokenType.BEARER)
        requestBuilder.setToken("Invalid JWS Token")
        val request = requestBuilder.build()
        val uisResponse = UserSessionService.SessionValidationResponse(
                success = false,
                message = "Token validation failed"
        )
        every { userSessionService.isValidSession(request) } returns uisResponse

        val response = blockingStub.validateUserSession(request)

        assertFalse(response.success)
        assertEquals("Token validation failed", response.message)
    }

    @AfterAll
    fun teardown() {
        unmockkAll()
    }
}