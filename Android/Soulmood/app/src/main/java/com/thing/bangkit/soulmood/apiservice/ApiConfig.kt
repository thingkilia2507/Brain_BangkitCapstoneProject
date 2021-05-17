package com.thing.bangkit.soulmood.apiservice

import com.thing.bangkit.soulmood.helper.RetrofitBuild

object ApiConfig {
    fun getRetrofitQuotes(): ApiService {
        return RetrofitBuild.instance("https://goquotes-api.herokuapp.com/api/v1/").create(ApiService::class.java)
    }

    fun getRetrofitSoulmood(): ApiService{
        return RetrofitBuild.instance("https://asia-southeast2-soulmood.cloudfunctions.net/").create(ApiService::class.java)
    }
}