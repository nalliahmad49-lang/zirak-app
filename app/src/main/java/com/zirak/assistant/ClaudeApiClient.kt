package com.zirak.assistant

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ClaudeApiClient {

    private val apiKey = "YOUR_ANTHROPIC_API_KEY_HERE"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json".toMediaType()

    fun sendMessage(userMessage: String, callback: (String?, String?) -> Unit) {
        val json = JSONObject().apply {
            put("model", "claude-sonnet-4-6")
            put("max_tokens", 1024)
            put("system", "تۆ یارمەتیدەرێکی زیرەکی کوردیت ناوت زیرەکە. هەمیشە بە کور
