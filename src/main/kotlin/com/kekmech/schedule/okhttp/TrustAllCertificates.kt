package com.kekmech.schedule.okhttp

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


fun OkHttpClient.Builder.trustAllSslCertificates() {
    // Create a trust manager that does not validate certificate chains

    // Create a trust manager that does not validate certificate chains
    val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) = Unit

            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) = Unit

            override fun getAcceptedIssuers(): Array<X509Certificate?>? = emptyArray()
        }
    )

    // Install the all-trusting trust manager

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    // Create an ssl socket factory with our all-trusting manager
    sslSocketFactory(sslContext.socketFactory, trustAllCerts.first() as X509TrustManager)
    hostnameVerifier(HostnameVerifier { _, _ -> true })
}
