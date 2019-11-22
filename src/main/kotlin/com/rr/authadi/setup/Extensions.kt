package com.rr.authadi.setup

fun String.nullIfEmpty() : String? {
    return if(this.isNullOrBlank()) {
        null
    } else {
        this
    }
}