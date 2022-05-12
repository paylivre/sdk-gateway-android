package com.paylivre.sdk.gateway.android.utils

fun getRandomString(length: Int?) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length!!)
        .map { allowedChars.random() }
        .joinToString("")
}
