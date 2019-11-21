package com.rr.authadi.injection.component

import com.rr.authadi.controller.ServiceController
import com.rr.authadi.dao.AbstractDao
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.service.UserImmigrationImpl
import com.rr.authadi.setup.JdbiHandle
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class])
interface ServiceComponent {
    fun inject(serviceController : ServiceController)
    fun inject(dataSource: JdbiHandle)
    fun inject(jdbi: AbstractDao)
    fun inject(userIdentityService: UserImmigrationImpl)
}