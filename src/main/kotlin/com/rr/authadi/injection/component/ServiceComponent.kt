package com.rr.authadi.injection.component

import com.rr.authadi.controller.ServiceController
import com.rr.authadi.dao.UserDao
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.setup.RequeryHandle
import com.zaxxer.hikari.HikariDataSource
import dagger.Component
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class])
interface ServiceComponent {
    fun inject(serviceController : ServiceController)
    fun inject(dataSource: RequeryHandle)
    fun inject(dataHandle: UserDao)
}