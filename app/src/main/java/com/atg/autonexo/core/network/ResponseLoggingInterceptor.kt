package com.atg.autonexo.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class ResponseLoggingInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "ResponseLogging"
    }
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Log request
        Log.d(TAG, "Request: ${request.method} ${request.url}")
        
        // Log response
        Log.d(TAG, "Response code: ${response.code}")
        Log.d(TAG, "Response message: ${response.message}")
        
        // Log response body
        val responseBody = response.body
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body
            val buffer = source.buffer
            val responseBodyString = buffer.clone().readUtf8()
            Log.d(TAG, "Response body: $responseBodyString")
        }
        
        return response
    }
}
