package com.thing.bangkit.soulmood.model

data class QuoteData(
    val quotes:List<QuoteOfTheDay>
)
data class QuoteOfTheDay(
    val text : String,
    val author :String
)
