package com.thing.bangkit.soulmood.apiservice

import com.thing.bangkit.soulmood.helper.RetrofitBuild

object ApiConfig {
    fun getRetrofitQuotes(): ApiService {
        return RetrofitBuild.getInstance("https://api.quotable.io/").create(ApiService::class.java)
    }
    fun getRetrofitSoulmood(): ApiService{
        return RetrofitBuild.getInstance("").create(ApiService::class.java)
    }
}