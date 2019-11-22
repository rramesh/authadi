package com.rr.authadi.dao

import javax.inject.Singleton

@Singleton
class DaoImpl : AbstractDao() {
    fun getUserIdentityDao() : UserIdentityDao {
        return jdbi.onDemand(UserIdentityDao::class.java)
    }
}