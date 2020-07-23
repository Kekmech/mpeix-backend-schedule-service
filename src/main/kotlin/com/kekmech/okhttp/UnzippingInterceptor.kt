package com.kekmech.okhttp

import okhttp3.*
import okhttp3.internal.http.*
import okio.*

class UnzippingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request());
        return unzip(response);
    }

    private fun unzip(response: Response): Response {
        if (response.body == null) {
            return response
        }

        //check if we have gzip response
        val contentEncoding = response.headers["Content-Encoding"];

        //this is used to decompress gzipped responses
        if (contentEncoding == null || contentEncoding != "gzip") {
            return response
        } else {
            val body = response.body as ResponseBody
            val contentLength = body.contentLength()
            val responseBody = GzipSource(body.source())
            val strippedHeaders = response.headers.newBuilder().build()
            return response.newBuilder().headers(strippedHeaders)
                .body(RealResponseBody(body.contentType().toString(), contentLength, responseBody.buffer()))
                .build()
        }
    }
}