package com.rr.authadi.dao

import com.rr.authadi.ServiceRunner
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Inject

abstract class AbstractDao {
    @Inject
    lateinit var dataHandle : KotlinEntityDataStore<Any>
    val data: KotlinReactiveEntityStore<Any>

    init {
        ServiceRunner.serviceComponent.inject(this)
        data = KotlinReactiveEntityStore(dataHandle)
    }
}