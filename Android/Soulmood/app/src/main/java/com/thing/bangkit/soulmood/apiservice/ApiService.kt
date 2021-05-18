package com.thing.bangkit.soulmood.apiservice

import com.thing.bangkit.soulmood.model.BadWordResponse
import com.thing.bangkit.soulmood.model.ChatbotResponse
import com.thing.bangkit.soulmood.model.MoodDetectorResponse
import com.thing.bangkit.soulmood.model.QuoteData
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("random")
    suspend fun getDialyQuote(@Query("count") count:Int):Response<QuoteData>

    @FormUrlEncoded
    @POST("chatbot")
    suspend fun reqChatbotResponse(@Field("name") name:String, @Field("message") message:String):Response<ChatbotResponse>

    @FormUrlEncoded
    @POST("kataKasar")
    suspend fun checkBadWordResponse(@Field("message") message:String):Response<BadWordResponse>

    @FormUrlEncoded
    @POST("moodDetector")
    suspend fun moodDetectorResponse(@Field("message") message:String):Response<MoodDetectorResponse>

}