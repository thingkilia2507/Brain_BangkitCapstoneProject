package com.thing.bangkit.soulmood.model

data class ChatbotResponse (
    val message : ArrayList<String>,
    val suggestion : ArrayList<String>
)