package com.thing.bangkit.soulmood.utils

import com.thing.bangkit.soulmood.apiservice.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getRetrofit(): ApiService {
        return Retrofit.Builder().baseUrl("https://goquotes-api.herokuapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}