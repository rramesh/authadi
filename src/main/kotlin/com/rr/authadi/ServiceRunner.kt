package com.rr.authadi

import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.component.ServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.AppConfig.dbProperties
import com.rr.authadi.setup.JdbiHandle
import org.flywaydb.core.Flyway
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServiceRunner {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(ServiceRunner::class.java)
        @JvmStatic lateinit var serviceComponent: ServiceComponent
    }

    init {
        serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(ServiceModule())
                .build()
    }

    fun run() {
        runMigration()
        logger.info("Service ran and exited smooth. This log will go away when service is ready functionally")
    }

    private fun runMigration() {
        val props = dbProperties()
        val flyway:Flyway = Flyway.configure()
                .dataSource(
                        "${props.get("db.url")}?currentSchema=${props.get("db.schemas")}",
                        props.get("db.user"),
                        props.get("db.password")
                ).load()
        flyway.migrate()
    }

    val greeting: String
        get() {
            return "Hello world."
        }
}
