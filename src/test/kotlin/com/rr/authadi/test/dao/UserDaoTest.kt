package com.rr.authadi.test.dao

import com.rr.authadi.dao.UserDao
import com.rr.authadi.entities.vault.User
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest : AbstractDaoTest(){
    private val userDao: UserDao = handle.onDemand(UserDao::class.java)

    @Test
    fun `it should return null if phone number not found`() {
        val expectedUser = userDao.findByPhoneNumber("UnknownNumber")
        assertNull(expectedUser)
    }

    @Test
    fun `it should find a User by phone number`() {
            val actualUser = randomUser()
            handle.withHandleUnchecked { handle ->
                handle.createUpdate("insert into users(user_uuid, phone_number) values('${actualUser.userUuid}', '${actualUser.phoneNumber}')")
                        .execute()
            }
            val expectedUser = userDao.findByPhoneNumber("123456789")
            assertEquals(expectedUser?.phoneNumber, actualUser.phoneNumber)
            assertEquals(expectedUser?.userUuid, actualUser.userUuid)
    }

    @Test
    fun `it should insert phone and user uuid and allow DB to generate rest`() {
        val success = userDao.addWithDefaults(userUuid = "RandomUUID4242", phoneNumber = "7389200303")
        val expectedUser = userDao.findByPhoneNumber("7389200303")
        assertTrue { success }
        assertEquals(expectedUser?.userUuid, "RandomUUID4242")
        assertEquals(expectedUser?.phoneNumber, "7389200303")
    }

    @Test
    fun `it should throw unique constraint exception when phone_number exists`() {
        userDao.addWithDefaults(userUuid = "RandomUUID4242", phoneNumber = "7389200303")
        assertThrows<UnableToExecuteStatementException> {
            userDao.addWithDefaults(userUuid = "RandomUUID6734", phoneNumber = "7389200303")
        }
    }

    @Test
    fun `it should throw unique constraint exception when user_uuid exists`() {
        userDao.addWithDefaults(userUuid = "RandomUUID4242", phoneNumber = "7389200303")
        assertThrows<UnableToExecuteStatementException> {
            userDao.addWithDefaults(userUuid = "RandomUUID4242", phoneNumber = "5534200303")
        }
    }

    @Test
    fun `it should insert User with all fields provided`() {
        val success = userDao.add(
                userUuid = "RandomUUID8758",
                phoneNumber = "7384900303",
                password = "ExtraordinaryPassword",
                clientId = "8348922",
                secret = "Hush its a secret"
        )
        val expectedUser = userDao.findByPhoneNumber("7384900303")
        assertTrue { success }
        assertEquals(expectedUser?.userUuid, "RandomUUID8758")
        assertEquals(expectedUser?.phoneNumber, "7384900303")
        assertEquals(expectedUser?.password, "ExtraordinaryPassword")
        assertEquals(expectedUser?.clientId, "8348922")
        assertEquals(expectedUser?.secret, "Hush its a secret")
        assertEquals(expectedUser?.isActive, true)
    }

    @Test
    fun `it should return User with given phone number and password`() {
        userDao.add(
                userUuid = "RandomUUID8783",
                phoneNumber = "7383839303",
                password = "ExtraordinaryPassword",
                clientId = "83438322",
                secret = "I know what you did last summer"
        )
        val expectedUser = userDao.authenticate(phoneNumber = "7383839303", password = "ExtraordinaryPassword")
        assertNotNull(expectedUser)
    }

    @Test
    fun `it should return null with given phone number and password`() {
        userDao.add(
                userUuid = "RandomUUID32483",
                phoneNumber = "73123949303",
                password = "ExtraordinaryPassword",
                clientId = "83438322",
                secret = "I know what you did last summer"
        )
        val expectedUser = userDao.authenticate(phoneNumber = "73123949303", password = "ExtraPassword")
        assertNull(expectedUser)
    }

    private fun randomUser() : User {
        return User(
            phoneNumber = "123456789",
            userUuid = "UserUUID"
        )
    }
}
