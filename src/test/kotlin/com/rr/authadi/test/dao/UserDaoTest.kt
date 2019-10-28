package com.rr.authadi.test.dao

import com.rr.authadi.dao.UserDao
import com.rr.authadi.entities.vault.User
import com.rr.authadi.entities.vault.UserEntity
import com.rr.authadi.test.ToolBox
import io.requery.reactivex.KotlinReactiveEntityStore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {
    private val dataHandle = ToolBox.dataHandle
    private val data: KotlinReactiveEntityStore<Any> get() = KotlinReactiveEntityStore(dataHandle)

    @Test
    fun findOneUserByPhoneNumber() {
        data.withTransaction {
            val expectedUser = randomUser()
            val udao = UserDao()
            val actualUser = udao.findUserByPhoneNumber("123456789")
            assertEquals(expectedUser, actualUser)
            this.transaction.rollback()
        }
    }

    @Test
    fun findNoUserByPhoneNumber() {
        assertNull(UserDao().findUserByPhoneNumber("InvalidNumber"))
    }

    internal fun randomUser() : User {
        val user = UserEntity()
        user.phone_number = "123456789"
        user.user_uuid = "UserUUID"
        data.insert(user)
        return user
    }
}