package com.rr.authadi.injection.module

import com.rr.authadi.dao.DaoImpl
import com.rr.authadi.dao.UserIdentityDao
import com.rr.authadi.service.UserIdentityService
import com.rr.authadi.setup.JdbiHandle
import com.rr.authadi.setup.Repository
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import org.jdbi.v3.core.Jdbi
import javax.inject.Singleton

@Module
class ServiceModule {
    @Provides
    @Singleton
    fun providesDataSource(): HikariDataSource {
        return Repository().dataSource
    }

    @Provides
    @Singleton
    fun providesJdbi(): Jdbi {
        return JdbiHandle().getJdbiHandle()
    }

    @Provides
    @Singleton
    fun providesUserIdentityService(): UserIdentityService {
        return UserIdentityService()
    }

    @Provides
    @Singleton
    fun providesUserIdentityDao(): UserIdentityDao {
        return DaoImpl().getUserIdentityDao()
    }
}
