package com.example.stocktracker.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.*

object NetworkClient {
    private const val cacheSize = (10 * 1024 * 1024).toLong()
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String, context: Context): Retrofit {
        val cache = Cache(context.cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                request =
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                chain.proceed(request)
            }

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
        return retrofit!!
    }
}