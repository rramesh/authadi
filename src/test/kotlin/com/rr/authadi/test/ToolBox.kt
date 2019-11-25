package com.rr.authadi.test

import com.rr.authadi.AuthadiRunner
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.AppConfig
import com.rr.authadi.setup.JdbiHandle
import org.flywaydb.core.Flyway
import org.jdbi.v3.core.Jdbi

object ToolBox {
    val dataHandle: Jdbi by lazy { jdbiHandle() }

    init {
        AuthadiRunner.serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(ServiceModule())
                .build()
    }

    private fun jdbiHandle(): Jdbi {
        val jdbi = JdbiHandle().getJdbiHandle()
        runMigration()
        return jdbi
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
