package com.example.smsbomber.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.smsbomber.databinding.ActivityMainBinding
import com.example.smsbomber.ui.viewmodel.SendSmsViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: SendSmsViewModel by viewModels()

    private var isAnonimChecked = false
    private var repeatCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.anonymitySwitch.setOnCheckedChangeListener { _, isChecked ->
            isAnonimChecked = isChecked
        }

        binding.repeatSlider.addOnChangeListener { _, value, _ ->
            repeatCount = value.toInt()
        }

        binding.sendSmsButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
            } else {
                val phoneNumber = binding.phoneNumberEditText.text.toString()
                val message = binding.messageEditText.text.toString()

                sendSms(phoneNumber, message,isAnonimChecked, repeatCount)
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String, anonim: Boolean, repeatCount: Int) {
        viewModel.sendSms(phoneNumber, message, anonim, repeatCount).observe(this, Observer { result ->
            if (result!!.isSuccess) {
                Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_SEND_SMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = binding.phoneNumberEditText.text.toString()
                val message = binding.messageEditText.text.toString()
                sendSms(phoneNumber, message, isAnonimChecked, repeatCount)
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_SEND_SMS = 1
    }
}
