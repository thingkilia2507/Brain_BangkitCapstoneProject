package com.thing.bangkit.soulmood.model

data class BadWordResponse (
    val message : String,  //message yang dibalikin ML yang ditampilin
    val status : Boolean //true -> badword, false -> aman
)