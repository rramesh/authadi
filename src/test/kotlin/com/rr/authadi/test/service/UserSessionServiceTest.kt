package com.rr.authadi.test.service

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.service.UserSessionService
import com.rr.proto.authadi.UserSessionRequest
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
class UserSessionServiceTest {
    @MockK
    private lateinit var userIdentityDao: UserIdentityDao

    @InjectMockKs
    private lateinit var userSessionService: UserSessionService

    @BeforeAll
    fun setup() {
        AuthadiRunner.logger = LoggerFactory.getLogger(UserSessionService::class.java)
        mockkObject(AuthadiRunner)
        every { AuthadiRunner.serviceComponent.inject(any() as UserSessionService) } just runs
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `it should fail if neither uuid or uRefId is present`() {
        val mockRequest = mockk<UserSessionRequest>()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.ID_NOT_SET
        val result = userSessionService.isValidSession(mockRequest)
        assertFalse(result.success)
        assertEquals("Either userKey or uRefId has to be set", result.message)
    }

    @Test
    fun `it should fail if token type is invalid`() {
        val mockRequest = mockk<UserSessionRequest>()
        val uuid = UUID.randomUUID().toString()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.UUID
        every { mockRequest.uuid } returns uuid
        every { mockRequest.tokenType } returns UserSessionRequest.TokenType.UNRECOGNIZED
        val result = userSessionService.isValidSession(mockRequest)
        assertFalse(result.success)
        assertEquals(
                "Invalid tokenType, should be one of ${UserSessionRequest.TokenType.values().joinToString()}",
                result.message
        )
    }

    @Test
    fun `it should fail if token blank`() {
        val mockRequest = mockk<UserSessionRequest>()
        val uRefId = UUID.randomUUID().toString()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.UREFID
        every { mockRequest.uRefId } returns uRefId
        every { mockRequest.tokenType } returns UserSessionRequest.TokenType.BEARER
        every { mockRequest.token } returns ""
        val result = userSessionService.isValidSession(mockRequest)
        assertFalse(result.success)
        assertEquals("Field token cannot be blank", result.message)
    }

    @Test
    fun `it should fail if user identity not found with uuid`() {
        val mockRequest = mockk<UserSessionRequest>()
        val uuid = UUID.randomUUID()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.UUID
        every { mockRequest.uuid } returns uuid.toString()
        every { mockRequest.tokenType } returns UserSessionRequest.TokenType.BEARER
        every { mockRequest.token } returns "JWS Token"
        every { userIdentityDao.findByUuid(uuid) } returns null
        val result = userSessionService.isValidSession(mockRequest)
        assertFalse(result.success)
        assertEquals("Could not find user identity for the given request", result.message)
    }

    @Test
    fun `it should fail if user identity not found with uRefId`() {
        val mockRequest = mockk<UserSessionRequest>()
        val uRefId = UUID.randomUUID().toString()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.UREFID
        every { mockRequest.uRefId } returns uRefId
        every { mockRequest.tokenType } returns UserSessionRequest.TokenType.BEARER
        every { mockRequest.token } returns "JWS Token"
        every { userIdentityDao.findByUserByReferenceId(uRefId) } returns null
        val result = userSessionService.isValidSession(mockRequest)
        assertFalse(result.success)
        assertEquals("Could not find user identity for the given request", result.message)
    }

    @Test
    fun `it should fail if jws validation fails`() {
        val mockRequest = mockk<UserSessionRequest>()
        val uuid = UUID.randomUUID()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.UUID
        every { mockRequest.uuid } returns uuid.toString()
        every { mockRequest.tokenType } returns UserSessionRequest.TokenType.BEARER
        every { mockRequest.token } returns "JWS Token"
        val mockUserIdentity = mockk<UserIdentity>()
        every { userIdentityDao.findByUuid(uuid) } returns mockUserIdentity
        every { mockUserIdentity.validateJws("JWS Token") } returns false
        val result = userSessionService.isValidSession(mockRequest)
        assertFalse(result.success)
        assertEquals("Token validation failed", result.message)
    }

    @Test
    fun `it should succeed with successful jws validation`() {
        val mockRequest = mockk<UserSessionRequest>()
        val uuid = UUID.randomUUID()
        every { mockRequest.idCase } returns UserSessionRequest.IdCase.UUID
        every { mockRequest.uuid } returns uuid.toString()
        every { mockRequest.tokenType } returns UserSessionRequest.TokenType.BEARER
        every { mockRequest.token } returns "JWS Token"
        val mockUserIdentity = mockk<UserIdentity>()
        every { userIdentityDao.findByUuid(uuid) } returns mockUserIdentity
        every { mockUserIdentity.validateJws("JWS Token") } returns true
        val result = userSessionService.isValidSession(mockRequest)
        assertTrue(result.success)
        assertEquals("Valid Token", result.message)
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }
}