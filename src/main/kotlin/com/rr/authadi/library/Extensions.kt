package com.rr.authadi.library

fun String.nullIfEmpty() : String? {
    return if(this.isNullOrBlank()) {
        null
    } else {
        this
    }
}