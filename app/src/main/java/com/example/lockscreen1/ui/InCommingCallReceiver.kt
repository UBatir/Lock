package com.example.lockscreen1.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lockscreen1.extentions.ACCEPT_CALL
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.extentions.DECLINE_CALL

open class InCommingCallReceiver(): BroadcastReceiver(){
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACCEPT_CALL -> CallManager.accept()
            DECLINE_CALL -> CallManager.reject()
        }
    }

}