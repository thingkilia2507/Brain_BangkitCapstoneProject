package com.thing.bangkit.soulmood.model

data class ChatGroup(
    val id: String,
    val group_name: String,
)


data class ChatMessage(
    val id: String,
    val sender: String,
    val email: String? = null,
    val ori_message: String? = null,
    val ai_message: String,
    val created_at: String,
    val deleted_at: String? = null,
    val status: String
)