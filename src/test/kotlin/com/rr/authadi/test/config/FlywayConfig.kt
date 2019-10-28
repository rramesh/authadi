package com.rr.authadi.test.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import java.util.Properties

class FlywayConfig(private val dbConfig: Map<String, String>, private val _prefix: String? = "") {
    private val flywayProperties = Properties()

    val config by lazy{ loadFlywayConfig() }

    private fun loadFlywayConfig() : Flyway {
        val sanePrefix = _prefix ?: ""
        val config = FluentConfiguration().apply {
            findFlywayProperties(sanePrefix)
            configuration(flywayProperties)
        }
        config.validateOnMigrate(true)
        return Flyway(config)
    }
    private fun findFlywayProperties(prefix: String) {
        val sanePrefix = if (prefix.isBlank()) "" else "{$prefix}."
        dbConfig.filterKeys { it.startsWith("${sanePrefix}db", ignoreCase = true) }
                .mapKeys { envToProp(it.key, sanePrefix) }
                .forEach { (k, v) -> flywayProperties.setProperty(k,v)}
    }

    private fun envToProp(key: String, prefix: String): String {
        return key.replace("${prefix}db", "flyway", ignoreCase = true)
    }
}