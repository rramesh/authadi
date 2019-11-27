package com.rr.authadi.test.controller

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.controller.UserAuthenticationController
import com.rr.authadi.service.UserAuthenticationService
import com.rr.proto.authadi.PasswordAuthenticationRequest
import com.rr.proto.authadi.UserAuthenticationGrpc
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthenticationControllerTest {
    @MockK
    private lateinit var userAuthenticationService: UserAuthenticationService

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
                token = "JWS Token"
        )
        every { userAuthenticationService.passwordAuthenticate(request) } returns uisResponse

        val response = blockingStub.passwordAuthenticate(requestBuilder.build())

        assertTrue(response.success)
        assertEquals("Successfully Authenticated", response.message)
        assertEquals("UserUUID", response.uuid)
        assertEquals("UserRefId", response.uRefId)
        assertEquals("JWS Token", response.bearerToken)
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
                token = ""
        )
        every { userAuthenticationService.passwordAuthenticate(request) } returns uisResponse

        val response = blockingStub.passwordAuthenticate(requestBuilder.build())

        assertFalse(response.success)
        assertEquals("Authentication Failed", response.message)
        assertEquals("", response.uuid)
        assertEquals("", response.uRefId)
        assertEquals("", response.bearerToken)
    }

    @AfterAll
    fun teardown() {
        unmockkAll()
    }
}