package com.rr.authadi.injection.component

import com.rr.authadi.controller.UserAuthenticationController
import com.rr.authadi.controller.UserImmigrationController
import com.rr.authadi.dao.AbstractDao
import com.rr.authadi.injection.module.ServiceModule
import com.rr.authadi.service.UserAuthenticationService
import com.rr.authadi.service.UserIdentityService
import com.rr.authadi.service.UserSessionService
import com.rr.authadi.setup.JdbiHandle
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class])
interface ServiceComponent {
    fun inject(jdbiHandle: JdbiHandle)
    fun inject(abstractDao: AbstractDao)
    fun inject(userIdentityService: UserImmigrationController)
    fun inject(userAuthenticationController: UserAuthenticationController)
    fun inject(userIdentityService: UserIdentityService)
    fun inject(userAuthenticationService: UserAuthenticationService)
    fun inject(userSessionService: UserSessionService)
}