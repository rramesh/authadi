package com.rr.authadi.test

import com.rr.authadi.ServiceRunner
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.AppConfig
import com.rr.authadi.setup.JdbiHandle
import org.flywaydb.core.Flyway
import org.jdbi.v3.core.Jdbi

object ToolBox {
    private val dbConfig = AppConfig.dbProperties()
    val dataHandle: Jdbi
    init {
        ServiceRunner.serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(ServiceModule())
                .build()
        dataHandle = JdbiHandle().getJdbiHandle()
        runMigration()
    }

    private fun runMigration() {
        val props = AppConfig.dbProperties()
        val flyway: Flyway = Flyway.configure()
                .dataSource(
                        "${props.get("db.url")}?currentSchema=${props.get("db.schemas")}",
                        props.get("db.user"),
                        props.get("db.password")
                ).load()
        flyway.migrate()
    }
}
