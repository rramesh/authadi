package com.rr.authadi.dao

import com.rr.authadi.ToolBox
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {
    val dataSource = ToolBox.dataSource
    @Test
    fun greetTest() {
        val dao = UserDao()
        assertEquals("Hello from UserDao", dao.greet())
    }

    @Test
    fun `3 + 2 = 5`() {
        assertEquals(5, 3+2)
    }
}