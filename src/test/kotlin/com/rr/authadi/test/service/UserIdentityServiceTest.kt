package com.rr.authadi.test.service

import com.rr.authadi.ServiceRunner
import com.rr.authadi.ServiceRunner.Companion.logger
import com.rr.authadi.dao.AbstractDao
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.exception.UserIdentityAuthenticationException
import com.rr.authadi.service.UserIdentityService
import com.rr.authadi.service.library.JwtHelper
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIdentityServiceTest {

    @MockK
    private lateinit var userIdentityDao: UserIdentityDao

    @InjectMockKs
    private lateinit var userIdentityService: UserIdentityService

    @BeforeAll
    fun setup() {
        logger = LoggerFactory.getLogger(UserIdentityService::class.java)
        mockkObject(ServiceRunner)
        every{ ServiceRunner.serviceComponent.inject(any() as AbstractDao)} just runs
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `it should insert user and return success`() {
        val userKey = "dumbo@mumbojumbo.com"
        val password = "HardToCrackPassword"
        val secret = "TerribleSecret"
        mockkObject(JwtHelper)
        every {JwtHelper.generateUserPassword()} returns password
        every {JwtHelper.generateUserSecret()} returns secret
        val expectedUuid = UUID.randomUUID()
        every {userIdentityDao.insert(
                userReferenceId = null, userKey = userKey,
                userSecondaryKey = null, password = password,
                clientId = any(), secret = secret, active = true
        )} returns expectedUuid
        val (actual, _) = userIdentityService.addUser(userKey = userKey)
        assertEquals(expectedUuid, actual)
    }

    @Test
    fun `it should return false with null uuid when insert throws exception`() {
        val userKey = "dumbo@mumbojumbo.com"
        val password = "HardToCrackPassword"
        val secret = "TerribleSecret"
        mockkObject(JwtHelper)
        every {JwtHelper.generateUserPassword()} returns password
        every {JwtHelper.generateUserSecret()} returns secret
        every {userIdentityDao.insert(
                userReferenceId = null, userKey = userKey,
                userSecondaryKey = null, password = password,
                clientId = any(), secret = secret, active = true
        )} throws UnableToExecuteStatementException("Constraint Exception Dude")
        val (actual, _) = userIdentityService.addUser(userKey = userKey)
        assertEquals(null, actual)
    }

    @Test
    fun `it should authenticate and return user identity object`() {
        val expectedUser = randomUser()
        val expectedUserIdentity = mockk<UserIdentity>()
        every {
            userIdentityDao.authenticatedUser(
                    expectedUser["user_key"] as String, expectedUser["password"] as String
            )
        } returns expectedUserIdentity
        val (actual,_) = userIdentityService.authenticate(
                userKey = expectedUser["user_key"] as String,
                password = expectedUser["password"] as String
        )
        assertEquals(expectedUserIdentity, actual)
    }

    @Test
    fun `it should authenticate and return false as success with null user identity`() {
        val expectedUser = randomUser()
        every {
            userIdentityDao.authenticatedUser(
                    userKey = expectedUser["user_key"] as String,
                    password = "UnimaginablePassword"
            )
        } returns null
        val (_, ex) = userIdentityService.authenticate(
                expectedUser["user_key"] as String,
                "UnimaginablePassword"
        )
        assertEquals("Authentication Failed", ex?.message)
    }

    private fun randomUser(
            userReferenceId: String? = null,
            userKey: String? = null,
            userSecondaryKey: String? = null,
            password: String? = null,
            clientId: UUID? = null,
            secret: String? = null,
            active: Boolean = true
    ) : Map<String, Any> {
        return mutableMapOf<String, Any>(
        "user_reference_id" to (userReferenceId ?: UUID.randomUUID().toString()),
        "user_key" to (userKey ?: UUID.randomUUID().toString()),
        "user_secondary_key" to (userSecondaryKey ?: UUID.randomUUID().toString()),
        "password" to (password ?: JwtHelper.generateUserPassword()),
        "client_id" to (clientId ?: UUID.randomUUID()),
        "secret" to (secret?: JwtHelper.generateUserSecret()),
        "active" to active
        )
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }
}