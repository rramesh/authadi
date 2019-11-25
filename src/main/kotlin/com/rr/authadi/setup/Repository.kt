package com.rr.authadi.setup

import com.rr.authadi.setup.AppConfig.dbProperties
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.inject.Singleton

@Singleton
class Repository() {
    val dataSource: HikariDataSource by lazy { hikariDataSource() }
    private fun hikariDataSource(): HikariDataSource {
        val config = HikariConfig()
        val props = dbProperties()

        config.jdbcUrl = props.get("db.url")
        config.username = props.get("db.user")
        config.password = props.get("db.password")
        config.schema = props.get("db.schemas")

        config.minimumIdle = Integer.max(Runtime.getRuntime().availableProcessors(), 2)
        config.maximumPoolSize = config.minimumIdle * 2
        config.idleTimeout = 180_000
        return HikariDataSource(config)
    }
}