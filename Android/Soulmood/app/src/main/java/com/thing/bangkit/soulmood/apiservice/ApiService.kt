package com.thing.bangkit.soulmood.apiservice

import androidx.lifecycle.LiveData
import com.thing.bangkit.soulmood.model.QuoteData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("random")
    suspend fun getDialyQuote(@Query("count") count:Int):Response<QuoteData>

}