package com.example.smsbomber.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SendSmsUseCase @Inject constructor(private val smsRepository: SmsRepository) {
    operator fun invoke(
        phoneNumber: String,
        message: String,
        anonim: Boolean,
        repeatCount: Int
    ): Result<Unit> {
        return try {
            CoroutineScope(Dispatchers.Main).launch {
                smsRepository.sendSms(phoneNumber, message, anonim, repeatCount)
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
