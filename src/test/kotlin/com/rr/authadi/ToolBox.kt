package com.rr.authadi

import com.rr.authadi.setup.AppConfig
import com.rr.authadi.config.FlywayConfig
import com.rr.authadi.setup.DataSource

object ToolBox {
    private val dbConfig = AppConfig.dbProperties("TEST")
    private val flywayConfig by lazy {
        FlywayConfig(dbConfig).config
    }
    val dataSource = DataSource("TEST").instance

    init {
        flywayConfig.migrate()
    }
}