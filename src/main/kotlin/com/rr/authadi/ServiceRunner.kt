package com.rr.authadi

import com.rr.authadi.dao.UserDao
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.component.ServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.AppConfig.dbProperties
import com.rr.authadi.setup.JdbiHandle
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

class ServiceRunner {
    companion object {
        val logger = LoggerFactory.getLogger(ServiceRunner::class.java)
        @JvmStatic lateinit var serviceComponent: ServiceComponent
    }

    init {
        serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(ServiceModule())
                .build()
    }

    fun run() {
        val handle = JdbiHandle().getJdbiHandle()
        runMigration()
        val dao = handle.onDemand(UserDao::class.java)
        val user = dao.findByPhoneNumber("1234567")
        println(user)
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
