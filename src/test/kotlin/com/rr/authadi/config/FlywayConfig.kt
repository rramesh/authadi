package com.rr.authadi.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import java.util.Properties

class FlywayConfig(private val dbConfig: Map<String, String>) {
    private val flywayProperties = Properties()

    val config by lazy{ loadFlywayConfig() }

    private fun loadFlywayConfig() : Flyway {
        val config = FluentConfiguration().apply {
            findFlywayProperties()
            configuration(flywayProperties)
        }
        config.validateOnMigrate(true)
        return Flyway(config)
    }
    private fun findFlywayProperties() {
        dbConfig.filterKeys { it.startsWith("test.db", ignoreCase = true) }
                .mapKeys { envToProp(it.key) }
                .forEach { (k, v) -> flywayProperties.setProperty(k,v)}
    }

    private fun envToProp(key: String): String {
        return key.replace("test.db", "flyway", ignoreCase = true)
    }
}