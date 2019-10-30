package com.rr.authadi.controller

import com.rr.authadi.ServiceRunner

class ServiceController {
    init {
        ServiceRunner.serviceComponent.inject(this)
    }
}