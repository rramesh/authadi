package com.rr.authadi.controller

import com.rr.authadi.ServiceRunner
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Inject

class ServiceController {
    init {
        ServiceRunner.serviceComponent.inject(this)
    }
}