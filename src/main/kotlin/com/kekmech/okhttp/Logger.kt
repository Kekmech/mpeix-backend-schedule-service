package com.kekmech.okhttp

import okhttp3.logging.*

object Logger : HttpLoggingInterceptor.Logger {

    override fun log(message: String) = println(message)
}