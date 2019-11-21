package com.rr.authadi.test.setup

import com.rr.authadi.test.ToolBox
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.AfterEach

abstract class DBTest {
    protected val handle = ToolBox.dataHandle
    @AfterEach
    fun cleanUp() {
        handle.withHandleUnchecked { handle ->
            val tables = listOf<String>("user_identities")
            tables.forEach { handle.createUpdate("truncate $it restart identity cascade").execute() }
        }
    }
}