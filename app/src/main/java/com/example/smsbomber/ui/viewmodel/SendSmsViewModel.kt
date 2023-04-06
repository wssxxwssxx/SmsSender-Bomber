package com.example.smsbomber.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smsbomber.domain.SendSmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SendSmsViewModel @Inject constructor(private val sendSmsUseCase: SendSmsUseCase) : ViewModel() {

    fun sendSms(phoneNumber: String, message: String, anonim: Boolean, repeatCount: Int): MutableLiveData<Result<Unit>?> {
        val result = MutableLiveData<Result<Unit>?>()

        if (isValidPhoneNumber(phoneNumber) && isValidMessage(message)) {
           result.value = sendSmsUseCase(phoneNumber, message, anonim, repeatCount)
        } else {
            result.value = Result.failure(IllegalArgumentException("Invalid phone number or message"))
        }
        return result
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = "^[+]?[0-9]{10,13}\$".toRegex()
        return phoneNumber.matches(regex)
    }

    private fun isValidMessage(message: String): Boolean {
        return message.length in 1..160
    }
}
