package com.rr.authadi.setup

import com.rr.authadi.ServiceRunner
import com.zaxxer.hikari.HikariDataSource
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import com.rr.authadi.entities.vault.Models
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequeryHandle (){
    @Inject
    lateinit var dataSource: HikariDataSource
    init {
        ServiceRunner.serviceComponent.inject(this)
    }
    fun getDataHandle(): KotlinEntityDataStore<Any> {
        val kconfig = KotlinConfiguration(dataSource = dataSource, model = Models.DEFAULT)
        return KotlinEntityDataStore<Any>(kconfig)
    }
}