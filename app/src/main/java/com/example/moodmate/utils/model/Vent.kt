package com.example.moodmate.utils.model

data class Vent(
    val text: String = "",
    val timestamp: Long = 0L,
    val userId: String? = null,
    val replies: Map<String, Reply>? = null
)

