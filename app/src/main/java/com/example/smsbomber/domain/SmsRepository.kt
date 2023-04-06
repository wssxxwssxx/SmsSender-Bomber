package com.example.smsbomber.domain

interface SmsRepository {
    suspend fun sendSms(phoneNumber: String, message: String, anonim: Boolean, repeatCount: Int): Boolean
}
