package com.rr.authadi.test

import com.rr.authadi.ServiceRunner
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.component.ServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.AppConfig
import com.rr.authadi.test.config.FlywayConfig
import com.rr.authadi.setup.RequeryHandle
import io.requery.sql.KotlinEntityDataStore

object ToolBox {
    private val dbConfig = AppConfig.dbProperties()
    private val flywayConfig by lazy {
        FlywayConfig(dbConfig).config
    }
    @JvmStatic lateinit var serviceComponent: ServiceComponent
    val dataHandle: KotlinEntityDataStore<Any>
    init {
        ServiceRunner.serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(ServiceModule())
                .build()
        dataHandle = RequeryHandle().getDataHandle()
        flywayConfig.migrate()
    }
}