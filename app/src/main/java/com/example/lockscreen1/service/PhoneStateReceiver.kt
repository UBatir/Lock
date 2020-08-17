package com.example.lockscreen1.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast

open  class PhoneStateReceiver: BroadcastReceiver() {
    private var listener: ((Int) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {

            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Toast.makeText(context, "Ringing State Number", Toast.LENGTH_SHORT).show()
                val isCalling = 1
                listener?.invoke(isCalling)

            }
    }

    fun setListener(listenerX: (Int) -> Unit) {
        listener = listenerX
    }
}
