package com.rr.authadi.dao

import com.rr.authadi.ServiceRunner
import org.jdbi.v3.core.Jdbi
import javax.inject.Inject

abstract class AbstractDao {
    @Inject
    lateinit var jdbi: Jdbi

    init{
        ServiceRunner.serviceComponent.inject(this)
    }
}
