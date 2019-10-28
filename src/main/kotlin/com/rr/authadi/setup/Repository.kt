package com.rr.authadi.setup

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.inject.Singleton

@Singleton
class Repository(private val _prefix: String? = null) {
    val dataSource : HikariDataSource by lazy { hikariDataSource() }
    private fun hikariDataSource() :  HikariDataSource{
        val config = HikariConfig()
        val props = AppConfig.dbProperties(_prefix)
        val sanePrefix : String = _prefix ?: ""
        val envPrefix = if (sanePrefix.isBlank()) "" else "${sanePrefix}.".toLowerCase()

        config.jdbcUrl = props.get("${envPrefix}db.url")
        config.username = props.get("${envPrefix}db.user")
        config.password = props.get("${envPrefix}db.password")
        config.schema = props.get("${envPrefix}db.schemas")

        config.minimumIdle = Integer.max(Runtime.getRuntime().availableProcessors(), 2)
        config.maximumPoolSize = config.minimumIdle * 2
        config.idleTimeout = 180_000
        return HikariDataSource(config)
    }
}