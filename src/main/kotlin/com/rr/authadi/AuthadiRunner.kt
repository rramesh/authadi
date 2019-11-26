package com.rr.authadi

import com.rr.authadi.controller.UserAuthenticationController
import com.rr.authadi.controller.UserImmigrationController
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.component.ServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.AppConfig
import com.rr.authadi.setup.AppConfig.dbProperties
import io.grpc.ServerBuilder
import org.flywaydb.core.Flyway
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthadiRunner {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(AuthadiRunner::class.java)
        @JvmStatic
        lateinit var serviceComponent: ServiceComponent
    }

    init {
        serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(ServiceModule())
                .build()
    }

    fun run() {
        runMigration()
        launchServer()
    }

    private fun launchServer() {
        val port = AppConfig.getServicePort()
        val uidServer = ServerBuilder.forPort(port)
                .addService(UserImmigrationController())
                .addService(UserAuthenticationController())
                .build()
        uidServer.start()
        logger.info("User Identity gRPC Service Started. Listening to port $port")
        Runtime.getRuntime().addShutdownHook(
                Thread { uidServer.shutdown() }
        )
        uidServer.awaitTermination()
        logger.info("User Identity gRPC Server shutdown successful.")
    }

    private fun runMigration() {
        val props = dbProperties()
        val flyway: Flyway = Flyway.configure()
                .dataSource(
                        "${props.get("db.url")}?currentSchema=${props.get("db.schemas")}",
                        props.get("db.user"),
                        props.get("db.password")
                ).load()
        flyway.migrate()
    }
}
