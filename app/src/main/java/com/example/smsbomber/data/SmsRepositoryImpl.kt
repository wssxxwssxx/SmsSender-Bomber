package com.example.smsbomber.data

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.example.smsbomber.domain.SmsRepository
import com.example.smsbomber.util.registerBroadcastReceiver
import com.example.smsbomber.util.toStringRepresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class SmsRepositoryImpl @Inject constructor(private val context: Context) : SmsRepository {
    private lateinit var threadPool: ExecutorService

    override suspend fun sendSms(phoneNumber: String, message: String, anonim: Boolean, repeatCount: Int): Boolean {
        threadPool = Executors.newFixedThreadPool(repeatCount)
        var coroutineDispatcher = threadPool.asCoroutineDispatcher()
        return try {
            CoroutineScope(coroutineDispatcher).launch {
                val task1 = async {
                    if (!anonim) {
                        sendNormalSms(phoneNumber, message)
                    } else {
                        sendAnonimSms(phoneNumber, message)
                    }
                }

                val task2 = async {
                    if (!anonim) {
                        sendNormalSms(phoneNumber, message)
                    } else {
                        sendAnonimSms(phoneNumber, message)
                    }
                }

                task1.await()
                task2.await()
            }.join()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            coroutineDispatcher.close()
        }
    }

    private fun sendNormalSms(phoneNumber: String, message: String): Boolean {
        val smsManager = SmsManager.getDefault()
        val sentPI = PendingIntent.getBroadcast(context, 0, Intent(SENT), 0)
        val deliveredPI = PendingIntent.getBroadcast(context, 0, Intent(DELIVERED), 0)

        context.registerBroadcastReceiver(SENT) { resultCode ->
            Toast.makeText(
                context,
                if (resultCode == Activity.RESULT_OK) "SMS sent" else "SMS not sent",
                Toast.LENGTH_SHORT
            ).show()
        }

        context.registerBroadcastReceiver(DELIVERED) { resultCode ->
            Toast.makeText(
                context,
                if (resultCode == Activity.RESULT_OK) "SMS delivered" else "SMS not delivered",
                Toast.LENGTH_SHORT
            ).show()
        }

        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
        return true
    }

    private fun sendAnonimSms(phoneNumber: String, message: String): Boolean {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("id", "50513")
            .add("key", "91CE1A3274A66C70")
            .add("to", phoneNumber)
            .add("from", "SMS-INFO")
            .add("text", message)
            .build()
        val request = Request.Builder()
            .url("https://api.bytehand.com/send")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("onFailure", "error: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("isSuccessful", "Успешная отправка SMS")
                } else {
                    Log.d("isNotSuccessful", "Не успешная отправка SMS: ${requestBody.toStringRepresentation()}")
                }
            }
        })
        return true
    }

    companion object {
        private const val SENT = "SMS_SENT"
        private const val DELIVERED = "SMS_DELIVERED"
    }
}
