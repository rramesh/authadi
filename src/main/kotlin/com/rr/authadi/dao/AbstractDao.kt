package com.rr.authadi.dao

import com.rr.authadi.AuthadiRunner
import org.jdbi.v3.core.Jdbi
import javax.inject.Inject

abstract class AbstractDao {
    @Inject
    lateinit var jdbi: Jdbi

    init {
        AuthadiRunner.serviceComponent.inject(this)
    }
}
