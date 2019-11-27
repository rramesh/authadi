package com.rr.authadi.test.service

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.service.UserAuthenticationService
import com.rr.proto.authadi.PasswordAuthenticationRequest
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthenticationServiceTest {
    @MockK
    private lateinit var userIdentityDao: UserIdentityDao

    @InjectMockKs
    private lateinit var userAuthenticationService: UserAuthenticationService

    @BeforeAll
    fun setup() {
        AuthadiRunner.logger = LoggerFactory.getLogger(UserAuthenticationService::class.java)
        mockkObject(AuthadiRunner)
        every { AuthadiRunner.serviceComponent.inject(any() as UserAuthenticationService) } just runs
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `it should fail with validation error when user key is empty`() {
        val mockRequest = mockk<PasswordAuthenticationRequest>()
        val userKey = ""
        val password = "HardToCrackPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password

        val response = userAuthenticationService.passwordAuthenticate(mockRequest)

        assertFalse(response.success)
        assertEquals("", response.token)
        assertEquals(
                "Missing userKey. userKey is mandatory",
                response.message
        )
    }

    @Test
    fun `it should return true upon successful  - null userRefernceId case`() {
        val mockRequest = mockk<PasswordAuthenticationRequest>()
        val userKey = "doll@roll.com"
        val password = "DollyPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password

        val expectedUserIdentity = mockk<UserIdentity>()
        val uuid = UUID.randomUUID()
        every { expectedUserIdentity.uuid } returns uuid
        every { expectedUserIdentity.userReferenceId} returns null
        every { expectedUserIdentity.getJws() } returns "JWS Token"
        every {
            userIdentityDao.authenticatedUser(userKey, password)
        } returns expectedUserIdentity
        val response = userAuthenticationService.passwordAuthenticate(
                mockRequest
        )
        assertTrue(response.success)
        assertEquals("Successfully Authenticated", response.message)
        assertEquals(uuid.toString(), response.uuid)
        assertEquals("", response.uRefId)
        assertEquals("JWS Token", response.token)
    }

    @Test
    fun `it should return true upon successful authentication`() {
        val mockRequest = mockk<PasswordAuthenticationRequest>()
        val userKey = "doll@roll.com"
        val password = "DollyPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password

        val expectedUserIdentity = mockk<UserIdentity>()
        val uuid = UUID.randomUUID()
        every { expectedUserIdentity.uuid } returns uuid
        every { expectedUserIdentity.userReferenceId} returns "UserReferenceId"
        every { expectedUserIdentity.getJws() } returns "JWS Token"
        every {
            userIdentityDao.authenticatedUser(userKey, password)
        } returns expectedUserIdentity
        val response = userAuthenticationService.passwordAuthenticate(
                mockRequest
        )
        assertTrue(response.success)
        assertEquals("Successfully Authenticated", response.message)
        assertEquals(uuid.toString(), response.uuid)
        assertEquals("UserReferenceId", response.uRefId)
        assertEquals("JWS Token", response.token)
    }

    @Test
    fun `it should return false upon unsuccessful authentication`() {
        val mockRequest = mockk<PasswordAuthenticationRequest>()
        val userKey = "dolly@roll.com"
        val password = "DollyNotPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password

        every {
            userIdentityDao.authenticatedUser(userKey, password)
        } returns null
        val response = userAuthenticationService.passwordAuthenticate(
                mockRequest
        )

        assertFalse(response.success)
        assertEquals("Authentication Failed", response.message)
        assertEquals("", response.uuid)
        assertEquals("", response.token)
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }
}