package com.thing.bangkit.soulmood.apiservice

import com.thing.bangkit.soulmood.model.BadWordResponse
import com.thing.bangkit.soulmood.model.ChatbotResponse
import com.thing.bangkit.soulmood.model.MoodDetectorResponse
import com.thing.bangkit.soulmood.model.QuoteOfTheDay
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("random")
    suspend fun getDialyQuote():Response<QuoteOfTheDay>

    @FormUrlEncoded
    @POST("chatbot")
    suspend fun reqChatbotResponse(@Field("name") name:String, @Field("message") message:String):Response<ChatbotResponse>

    @FormUrlEncoded
    @POST("/")
    suspend fun checkBadWordResponse(@Field("message") message:String):Response<BadWordResponse>

    @FormUrlEncoded
    @POST("moodDetector")
    suspend fun moodDetectorResponse(@Field("message") message:String):Response<MoodDetectorResponse>

}