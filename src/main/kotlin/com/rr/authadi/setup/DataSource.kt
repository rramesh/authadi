package com.rr.authadi.setup

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.inject.Singleton

@Singleton
class DataSource(private val prefix: String? = null) {
    val instance:HikariDataSource by lazy { getDataSource() }
    private fun getDataSource() :  HikariDataSource{
        val config = HikariConfig()
        val props = AppConfig.dbProperties(prefix)
        val envPrefix = if (prefix!!.isBlank()) "" else "${prefix}.".toLowerCase()

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