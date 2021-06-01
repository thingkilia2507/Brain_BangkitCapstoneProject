package com.thing.bangkit.soulmood.helper

import com.thing.bangkit.soulmood.apiservice.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuild {

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .build()


    private fun getInstance(URL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Volatile
    private var quotesService: ApiService? = null

    @JvmStatic
    fun instanceQuotesService(): ApiService {
        if (quotesService == null) {
            quotesService = getInstance("https://api.quotable.io/").create(ApiService::class.java)
        }
        return quotesService as ApiService
    }


    @Volatile
    private var baseService: ApiService? = null

    @JvmStatic
    fun instanceService(): ApiService {
        if (baseService == null) {
            baseService = getInstance("https://asia-southeast2-soulmood.cloudfunctions.net/").create(ApiService::class.java)
        }
        return baseService as ApiService
    }

    @Volatile
    private var badwordService: ApiService? = null

    fun instanceBadwordService(): ApiService {
        if (badwordService == null) {
            badwordService = getInstance("https://soulmood.uc.r.appspot.com").create(ApiService::class.java)
        }
        return badwordService as ApiService
    }
}