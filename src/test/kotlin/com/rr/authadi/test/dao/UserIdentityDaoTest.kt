package com.rr.authadi.test.dao

import com.rr.authadi.dao.UserIdentityDao
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIdentityDaoTest : AbstractDaoTest(){
    private val userIdentityDao: UserIdentityDao = handle.onDemand(UserIdentityDao::class.java)

    @Test
    fun `it should return null if user key not found`() {
        val expectedUser = userIdentityDao.findByUserKey("UnknownKey")
        assertNull(expectedUser)
    }

    @Test
    fun `it should find a User by user key`() {
            handle.withHandleUnchecked { handle ->
                handle.createUpdate("""
                    insert into user_identities(user_key, password, client_id, secret) 
                    values('dumbo@jumbo.com', 'HeeHaaW!', '${UUID.randomUUID()}', '${UUID.randomUUID()}')
                    """)
                        .execute()
            }
            val expectedUser = userIdentityDao.findByUserKey("dumbo@jumbo.com")
            assertNotNull(expectedUser)
    }

    @Test
    fun `it should find User by secondary key`() {
        userIdentityDao.insert(
                userKey = "xyz@example.com",
                userSecondaryKey = "7738893002",
                password = "Secret Password",
                clientId = UUID.randomUUID(),
                secret = UUID.randomUUID().toString()
        )
        val expectedUser = userIdentityDao.findByUserKey("xyz@example.com")
        val actualUser = userIdentityDao.findByUserPrimaryOrSecondaryKey("7738893002")
        assertEquals(expectedUser, actualUser)
    }

    @Test
    fun `it should return null if user cannot be found by primary or secondary key`() {
        val user = userIdentityDao.findByUserPrimaryOrSecondaryKey("NeitherWayExistsKey")
        assertNull(user)
    }

    @Test
    fun `it should return user given correct user key and password`() {
        val expectedUuid = userIdentityDao.insert(
                userKey = "abc@example.com",
                password = "Hush! its the Password",
                clientId = UUID.randomUUID(),
                secret = UUID.randomUUID().toString()
        )
        val actualUuid = userIdentityDao.authenticatedUser("abc@example.com", "Hush! its the Password")
        assertEquals(expectedUuid, actualUuid)
    }

    @Test
    fun `it should find a User by user reference id`() {
        val userReferenceId = UUID.randomUUID().toString()
        val clientId = UUID.randomUUID()
        val secret = UUID.randomUUID().toString()
        handle.withHandleUnchecked { handle ->
            handle.createUpdate("""
                    insert into user_identities(user_key, user_reference_id, password, client_id, secret) 
                    values('dumbo2@jumbo.com', '${userReferenceId}', 'HeeHaaW!', '${clientId}', '${secret}')
                    """)
                    .execute()
        }
        val expectedUser = userIdentityDao.findByUserByReferenceId(userReferenceId)
        assertEquals(clientId, expectedUser?.clientId)
        assertEquals(secret, expectedUser?.secret)
    }

    @Test
    fun `it should return null if user reference key not found`() {
        val userReferenceId = UUID.randomUUID().toString()
        val clientId = UUID.randomUUID()
        val secret = UUID.randomUUID().toString()
        //insert a default null user reference uuid record
        handle.withHandleUnchecked { handle ->
            handle.createUpdate("""
                    insert into user_identities(user_key, password, client_id, secret) 
                    values('dumbo3@jumbo.com', 'HeeHaaW!', '${clientId}', '${secret}')
                    """)
                    .execute()
        }
        val expectedUser = userIdentityDao.findByUserByReferenceId(userReferenceId)
        assertNull(expectedUser)
    }

    @Test
    fun `it should insert new user identity`() {
        val expectedUUID = userIdentityDao.insert(
                userKey = "xyz@example.com", password = "Secret Password",
                clientId = UUID.randomUUID(), secret = UUID.randomUUID().toString()
        )
        assertNotNull(expectedUUID)
    }

    @Test
    fun `it should throw unique constraint exception when userKey exists`() {
        userIdentityDao.insert(
                userKey = "RandomUUID4242", password = "Secret Password",
                clientId = UUID.randomUUID(), secret = UUID.randomUUID().toString()
        )
        assertThrows<UnableToExecuteStatementException> {
            userIdentityDao.insert(
                    userKey = "RandomUUID4242", password = "Changing Password",
                    clientId = UUID.randomUUID(), secret = UUID.randomUUID().toString()
            )
        }
    }
}
