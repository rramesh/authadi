package com.rr.authadi.test.dao

import com.rr.authadi.dao.UserDao
import com.rr.authadi.entities.vault.UserEntity
import com.rr.authadi.test.ToolBox
import io.requery.sql.KotlinEntityDataStore
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.inject.Inject
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {
    @BeforeAll
    fun setUp() {
        val user = UserEntity()
        user.phone_number = "123456789"
        user.user_uuid = "UserUUID"
        user.password = "SecretHush"
        user.client_id = "ClientID"
        user.secret = "ExtremelyConfidential"
        ToolBox.dataHandle.insert(user)
    }

    @Test
    fun testFindUserByPhoneNumber() {
        val udao = UserDao()
        val actualUser = udao.findUserByPhoneNumber("123456789")
        assertEquals("UserUUID", actualUser.user_uuid)
        assertEquals("SecretHush", actualUser.password)
        assertEquals("ClientID", actualUser.client_id)
        assertEquals("ExtremelyConfidential", actualUser.secret)
    }
}