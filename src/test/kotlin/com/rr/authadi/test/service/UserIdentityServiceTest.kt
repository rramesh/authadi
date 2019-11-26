package com.rr.authadi.test.service

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.AuthadiRunner.Companion.logger
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.library.JwtHelper
import com.rr.authadi.service.UserIdentityService
import com.rr.proto.authadi.UserImmigrationRequest
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.util.PSQLException
import org.postgresql.util.ServerErrorMessage
import org.slf4j.LoggerFactory
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIdentityServiceTest {
    val secret = "TerribleSecret"

    @MockK
    private lateinit var userIdentityDao: UserIdentityDao

    @InjectMockKs
    private lateinit var userIdentityService: UserIdentityService

    @BeforeAll
    fun setup() {
        logger = LoggerFactory.getLogger(UserIdentityService::class.java)
        mockkObject(AuthadiRunner)
        every { AuthadiRunner.serviceComponent.inject(any() as UserIdentityService) } just runs
        mockkObject(JwtHelper)
        every { JwtHelper.generateUserSecret() } returns secret
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `it should insert user and return success`() {
        val mockRequest = mockk<UserImmigrationRequest>()
        val userKey = "dumbo@mumbojumbo.com"
        val password = "HardToCrackPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password
        every { mockRequest invokeNoArgs ("getUserSecondaryKey") } returns ""
        every { mockRequest invokeNoArgs ("getUserReferenceId") } returns ""

        every { userIdentityDao.findByUserKey(userKey) } returns null

        val expectedUuid = UUID.randomUUID()
        every {
            userIdentityDao.insert(
                    userReferenceId = null, userKey = userKey,
                    userSecondaryKey = null, password = password,
                    clientId = any(), secret = secret, active = true
            )
        } returns expectedUuid

        val response = userIdentityService.addUser(mockRequest)

        assertTrue(response.success)
        assertEquals(expectedUuid.toString(), response.uuid)
        assertEquals("User Identity successfully added", response.message)
    }

    @Test
    fun `it should fail with validation error when user key is empty`() {
        val mockRequest = mockk<UserImmigrationRequest>()
        val userKey = ""
        val password = "HardToCrackPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password
        every { mockRequest invokeNoArgs ("getUserSecondaryKey") } returns ""
        every { mockRequest invokeNoArgs ("getUserReferenceId") } returns ""

        val response = userIdentityService.addUser(mockRequest)

        assertFalse(response.success)
        assertEquals("", response.uuid)
        assertEquals(
                "One or more required fields missing. userKey and password mandatory",
                response.message
        )
    }

    @Test
    fun `it should fail with validation error when password is empty`() {
        val mockRequest = mockk<UserImmigrationRequest>()
        val userKey = "dumbo@mumbojumbo.com"
        val password = ""

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password
        every { mockRequest invokeNoArgs ("getUserSecondaryKey") } returns ""
        every { mockRequest invokeNoArgs ("getUserReferenceId") } returns ""

        val response = userIdentityService.addUser(mockRequest)

        assertFalse(response.success)
        assertEquals("", response.uuid)
        assertEquals(
                "One or more required fields missing. userKey and password mandatory",
                response.message
        )
    }

    @Test
    fun `it should return false on duplicate user key insertion`() {
        val mockRequest = mockk<UserImmigrationRequest>()
        val userKey = "dumbo@mumbojumbo.com"
        val password = "HardToCrackPassword"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password
        every { mockRequest invokeNoArgs ("getUserSecondaryKey") } returns ""
        every { mockRequest invokeNoArgs ("getUserReferenceId") } returns ""

        val mockUserIdentity = mockk<UserIdentity>()
        every { userIdentityDao.findByUserKey(userKey) } returns mockUserIdentity

        val response = userIdentityService.addUser(mockRequest)
        assertFalse(response.success)
        assertEquals("", response.uuid)
        assertEquals("User Key $userKey already exists", response.message)
    }

    @Test
    fun `it should return false with empty uuid and error message when insert throws exception`() {
        val mockRequest = mockk<UserImmigrationRequest>()
        val userKey = "dumbo@mumbojumbo.com"
        val password = "HardToCrackPassword"
        val secret = "TerribleSecret"

        every { mockRequest invokeNoArgs ("getUserKey") } returns userKey
        every { mockRequest invokeNoArgs ("getPassword") } returns password
        every { mockRequest invokeNoArgs ("getUserSecondaryKey") } returns ""
        every { mockRequest invokeNoArgs ("getUserReferenceId") } returns ""

        every { userIdentityDao.findByUserKey(userKey) } returns null

        every {
            userIdentityDao.insert(
                    userReferenceId = null, userKey = userKey,
                    userSecondaryKey = null, password = password,
                    clientId = any(), secret = secret, active = true
            )
        } throws PSQLException(ServerErrorMessage("Some SQL Exception Dude"))
        val response = userIdentityService.addUser(mockRequest)
        assertFalse(response.success)
        assertEquals("", response.uuid)
        assertEquals("Server Error. Could not insert user identity", response.message)
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }
}