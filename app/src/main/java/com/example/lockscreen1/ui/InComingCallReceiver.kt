package com.example.lockscreen1.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.lockscreen1.extentions.ACCEPT_CALL
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.extentions.DECLINE_CALL

open class InComingCallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//            val state: String? = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
//            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                Toast.makeText(context, "Идет звонок", Toast.LENGTH_LONG).show()
        when (intent?.action) {
            ACCEPT_CALL -> CallManager.accept()
            DECLINE_CALL -> CallManager.reject()
        }
    }
}
