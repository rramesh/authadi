package com.rr.authadi

import com.rr.authadi.dao.UserDao
import com.rr.authadi.injection.component.DaggerServiceComponent
import com.rr.authadi.injection.component.ServiceComponent
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.JdbiHandle
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
        val dao = handle.onDemand(UserDao::class.java)
        val user = dao.findByPhoneNumber("1234567")
        println(user)
    }

    val greeting: String
        get() {
            return "Hello world."
        }
}
