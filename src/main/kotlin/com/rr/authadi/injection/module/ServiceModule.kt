package com.rr.authadi.injection.module

import com.rr.authadi.setup.Repository
import com.rr.authadi.setup.RequeryHandle
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Singleton

@Module
class ServiceModule {
    @Provides @Singleton fun providesDataSource() : HikariDataSource {
        return Repository().dataSource
    }

    @Provides @Singleton fun providesDataHandle() : KotlinEntityDataStore<Any> {
        return RequeryHandle().getDataHandle()
    }
}