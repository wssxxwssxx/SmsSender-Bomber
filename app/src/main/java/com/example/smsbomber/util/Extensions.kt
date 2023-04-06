package com.example.smsbomber.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import okhttp3.RequestBody

fun RequestBody.toStringRepresentation(): String {
    val buffer = okio.Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}

fun Context.registerBroadcastReceiver(action: String, onReceive: (Int) -> Unit) {
    registerReceiver(object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceive(resultCode)
            context.unregisterReceiver(this)
        }
    }, IntentFilter(action))
}
