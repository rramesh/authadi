package com.rr.authadi.entities.vault

import io.requery.*
import java.util.*

@Entity
@Table(name = "users")
interface User : Persistable{
    @get: Key
    @get: Generated
    val uuid: String

    var user_uuid: String
    var phone_number: String

    @set:Generated
    var password: String

    @set:Generated
    var client_id: String

    @set:Generated
    var secret: String

    @get: Generated
    val created_at: Date
    @get: Generated
    val updated_at: Date
}