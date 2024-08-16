package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.telephony.TelephonyManager
import android.telephony.SmsManager

class MissedCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if (phoneNumber != null) {
                    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    val isSmsEnabled = sharedPreferences.getBoolean("smsEnabled", false)
                    val smsMessage = sharedPreferences.getString("smsMessage", "Sorry I missed your call. Please call after sometime.")

                    // Check if the phone is on silent and SMS functionality is enabled
                    if (isSmsEnabled && isPhoneSilent(context)) {
                        sendSms(context, phoneNumber, smsMessage!!)
                    }
                }
            }
        }
    }

    private fun isPhoneSilent(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT
    }

    private fun sendSms(context: Context, phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }
}
