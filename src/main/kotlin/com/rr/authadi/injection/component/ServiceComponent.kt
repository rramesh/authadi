package com.rr.authadi.injection.component

import com.rr.authadi.controller.UserImmigrationController
import com.rr.authadi.dao.AbstractDao
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.service.UserIdentityService
import com.rr.authadi.setup.JdbiHandle
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class])
interface ServiceComponent {
    fun inject(dataSource: JdbiHandle)
    fun inject(jdbi: AbstractDao)
    fun inject(userIdentityService: UserImmigrationController)
    fun inject(userIdentityDao: UserIdentityService)
}