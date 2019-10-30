package com.rr.authadi.setup

import com.rr.authadi.ServiceRunner
import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.transaction.SerializableTransactionRunner
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JdbiHandle {
    @Inject
    lateinit var dataSource: HikariDataSource
    init {
        ServiceRunner.serviceComponent.inject(this)
    }

    fun getJdbiHandle(): Jdbi {
        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.transactionHandler = SerializableTransactionRunner()
        return jdbi
    }
}
