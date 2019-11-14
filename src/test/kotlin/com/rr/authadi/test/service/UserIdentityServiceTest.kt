package com.rr.authadi.test.service

import com.rr.authadi.ServiceRunner
import com.rr.authadi.ServiceRunner.Companion.logger
import com.rr.authadi.dao.AbstractDao
import com.rr.authadi.dao.UserIdentityDao
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
    private var jwt = JwtHelper()

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
        every {jwt.generateUserPassword()} returns password
        every {jwt.generateUserSecret()} returns secret
        val expectedUuid = UUID.randomUUID()
        every {userIdentityDao.insert(
                userReferenceId = null, userKey = userKey,
                userSecondaryKey = null, password = password,
                clientId = any(), secret = secret, active = true
        )} returns expectedUuid
        val expected = Pair(true, expectedUuid)
        val actual = userIdentityService.addUser(userKey = userKey)
        assertEquals(expected, actual)
    }

    @Test
    fun `it should return false with null uuid when insert throws exception`() {
        val userKey = "dumbo@mumbojumbo.com"
        val password = "HardToCrackPassword"
        val secret = "TerribleSecret"
        every {jwt.generateUserPassword()} returns password
        every {jwt.generateUserSecret()} returns secret
        every {userIdentityDao.insert(
                userReferenceId = null, userKey = userKey,
                userSecondaryKey = null, password = password,
                clientId = any(), secret = secret, active = true
        )} throws UnableToExecuteStatementException("Constraint Exception Dude")
        val expected = Pair(false, null)
        val actual = userIdentityService.addUser(userKey = userKey)
        assertEquals(expected, actual)
    }

    @Test
    fun `it should authenticate and return true as success user uuid`() {
        val expectedUser = randomUser()
        val expectedUuid = UUID.randomUUID()
        every {
            userIdentityDao.authenticatedUser(
                    expectedUser["user_key"] as String, expectedUser["password"] as String
            )
        } returns expectedUuid
        val actual = userIdentityService.authenticate(
                userKey = expectedUser["user_key"] as String,
                password = expectedUser["password"] as String
        )
        assertEquals(Pair(true, expectedUuid), actual)
    }

    @Test
    fun `it should authenticate and return false as success with null uuid`() {
        val expectedUser = randomUser()
        every {
            userIdentityDao.authenticatedUser(
                    userKey = expectedUser["user_key"] as String,
                    password = "UnimaginablePassword"
            )
        } returns null
        val actual = userIdentityService.authenticate(
                expectedUser["user_key"] as String,
                "UnimaginablePassword"
        )
        assertEquals(Pair(false, null), actual)
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
        val localJwt = JwtHelper()
        return mutableMapOf<String, Any>(
        "user_reference_id" to (userReferenceId ?: UUID.randomUUID().toString()),
        "user_key" to (userKey ?: UUID.randomUUID().toString()),
        "user_secondary_key" to (userSecondaryKey ?: UUID.randomUUID().toString()),
        "password" to (password ?: localJwt.generateUserPassword()),
        "client_id" to (clientId ?: UUID.randomUUID()),
        "secret" to (secret?: localJwt.generateUserSecret()),
        "active" to active
        )
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }
}