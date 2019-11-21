package com.rr.authadi.test.setup

import com.rr.authadi.setup.AppConfig
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppConfigTest{
    private lateinit var mock: AppConfig
    private lateinit var dbProps: MutableMap<String, String>
    private lateinit var dbPropsWithPrefix: MutableMap<String, String>
    @BeforeAll
    fun setup() {
        mock = spyk<AppConfig>(recordPrivateCalls = true)
        dbProps = mutableMapOf<String, String>(
                "DB_URL" to "Database URL",
                "DB_USER" to "user",
                "DB_PASSWORD" to "password",
                "DB_SCHEMAS" to "schema"
        )
        dbPropsWithPrefix = mutableMapOf<String, String>(
                "TEST_DB_URL" to "Database URL",
                "TEST_DB_USER" to "user",
                "TEST_DB_PASSWORD" to "password",
                "TEST_DB_SCHEMAS" to "schema"
        )
    }

    @Test
    fun `test dbProperties from environment variables without prefix`() {
        every{ mock getProperty "envVars" } returns dbProps
        every{ mock getProperty "properties" } returns mutableMapOf<String, String>()
        every { mock getProperty "envPrefix" } returns ""

        val expectedProps = mutableMapOf(
                "db.url" to "Database URL",
                "db.user" to "user",
                "db.password" to "password",
                "db.schemas" to "schema"
        )

        val gotProps = mock.dbProperties()
        verify { mock invoke "filterDBProps" withArguments listOf(dbProps, "", "") }
        assertEquals(expectedProps, gotProps)
    }

    @Test
    fun `test dbProperties from environment variables with prefix`() {
        every{ mock getProperty "envVars" } returns dbPropsWithPrefix
        every{ mock getProperty "properties" } returns mutableMapOf<String, String>()
        every { mock getProperty "envPrefix" } returns "test"

        val expectedProps = mutableMapOf(
                "db.url" to "Database URL",
                "db.user" to "user",
                "db.password" to "password",
                "db.schemas" to "schema"
        )

        val gotProps = mock.dbProperties()
        verify { mock invoke "filterDBProps" withArguments listOf(dbPropsWithPrefix, "test", "_") }
        assertEquals(expectedProps, gotProps)
    }

    @Test
    fun `test dbProperties from property file without prefix`() {
        val expectedProps = mutableMapOf(
                "db.url" to "Database URL",
                "db.user" to "user",
                "db.password" to "password",
                "db.schemas" to "schema"
        )

        every{ mock getProperty "envVars" } returns mutableMapOf<String, String>()
        every{ mock getProperty "properties" } returns expectedProps
        every { mock getProperty "envPrefix" } returns ""

        val gotProps = mock.dbProperties()
        verify { mock invoke "filterDBProps" withArguments listOf(expectedProps, "", "") }
        assertEquals(expectedProps, gotProps)
    }

    @Test
    fun `test dbProperties from property file with prefix`() {
        val fileProps = mutableMapOf(
                "test.db.url" to "Database URL",
                "test.db.user" to "user",
                "test.db.password" to "password",
                "test.db.schemas" to "schema"
        )

        every{ mock getProperty "envVars" } returns mutableMapOf<String, String>()
        every{ mock getProperty "properties" } returns fileProps
        every { mock getProperty "envPrefix" } returns "test"

        val expectedProps = mutableMapOf(
                "db.url" to "Database URL",
                "db.user" to "user",
                "db.password" to "password",
                "db.schemas" to "schema"
        )

        val gotProps = mock.dbProperties()
        verify { mock invoke "filterDBProps" withArguments listOf(fileProps, "test", ".") }
        assertEquals(expectedProps, gotProps)
    }

    @Test
    fun `it should return the port number set in envvar`() {
        every{ mock getProperty "envVars" } returns mutableMapOf<String, String>(
                "SERVICE_PORT" to "7777"
        )
        every{ mock getProperty "properties" } returns mutableMapOf<String, String>()
        every { mock getProperty "envPrefix" } returns ""
        assertEquals(7777, mock.getServicePort())
    }

    @Test
    fun `it should return the port number set in property file`() {
        every{ mock getProperty "envVars" } returns mutableMapOf<String, String>()
        every{ mock getProperty "properties" } returns mutableMapOf<String, String>(
                "service.port" to "7878"
        )
        every { mock getProperty "envPrefix" } returns ""
        assertEquals(7878, mock.getServicePort())
    }

    @Test
    fun `it should default to port 15436`() {
        every{ mock getProperty "envVars" } returns mutableMapOf<String, String>()
        every{ mock getProperty "properties" } returns mutableMapOf<String, String>()
        every { mock getProperty "envPrefix" } returns ""
        assertEquals(15436, mock.getServicePort())
    }

    @Test
    fun `it should default to port 15436 if property set is invalid number`() {
        every{ mock getProperty "envVars" } returns mutableMapOf<String, String>()
        every{ mock getProperty "properties" } returns mutableMapOf<String, String>(
                "service.port" to "CanYouRun?"
        )
        every { mock getProperty "envPrefix" } returns ""
        assertEquals(15436, mock.getServicePort())
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }
}
