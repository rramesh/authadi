package com.rr.authadi.test.controller

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.controller.UserImmigrationController
import com.rr.authadi.service.UserIdentityService
import com.rr.proto.authadi.UserImmigrationGrpc
import com.rr.proto.authadi.UserImmigrationRequest
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserImmigrationControllerTest {
    @MockK
    private lateinit var userIdentityService: UserIdentityService

    @InjectMockKs
    private lateinit var userImmigrationController: UserImmigrationController

    @Rule
    private val grpcCleanup = GrpcCleanupRule()
    private val serverName = InProcessServerBuilder.generateName()
    private var server: Server
    private var channel: ManagedChannel

    init {
        mockkObject(AuthadiRunner)
        every { AuthadiRunner.serviceComponent.inject(any() as UserImmigrationController) } just runs
        MockKAnnotations.init(this, relaxUnitFun = true)
        server = grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor()
                .addService(userImmigrationController).build().start())
        channel = grpcCleanup.register(InProcessChannelBuilder
                .forName(serverName).directExecutor().build())
    }

    @Test
    fun `it should successfully insert user identity`() {
        val blockingStub = UserImmigrationGrpc.newBlockingStub(channel)
        val requestBuilder = UserImmigrationRequest.newBuilder()
        requestBuilder.setUserKey("johndoe@johnydoe.com")
        requestBuilder.setPassword("Hush Hush Password")
        requestBuilder.setUserReferenceId(UUID.randomUUID().toString())
        requestBuilder.setActive(true)
        val request = requestBuilder.build()
        val uisResponse = UserIdentityService.InsertResponse(
                success = true,
                message = "User Identity successfully added",
                uuid = UUID.randomUUID().toString()
        )
        every { userIdentityService.addUser(request) } returns uisResponse

        val response = blockingStub.addUserIdentity(requestBuilder.build())

        assertTrue(response.success)
        assertEquals("User Identity successfully added", response.message)
        assertEquals(uisResponse.uuid, response.uuid)
    }

    @Test
    fun `it should return failure inserting user identity`() {
        val blockingStub = UserImmigrationGrpc.newBlockingStub(channel)
        val requestBuilder = UserImmigrationRequest.newBuilder()
        requestBuilder.setUserKey("johndoe@johnydoe.com")
        requestBuilder.setPassword("Hush Hush Password")
        requestBuilder.setUserReferenceId(UUID.randomUUID().toString())
        requestBuilder.setActive(true)
        val request = requestBuilder.build()
        val uisResponse = UserIdentityService.InsertResponse(
                success = false,
                message = "User Key johndoe@johnydoe.com already exists",
                uuid = ""
        )
        every { userIdentityService.addUser(request) } returns uisResponse

        val response = blockingStub.addUserIdentity(requestBuilder.build())

        assertFalse(response.success)
        assertEquals("User Key johndoe@johnydoe.com already exists", response.message)
        assertEquals("", response.uuid)
    }
}