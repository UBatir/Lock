package com.example.lockscreen1.service

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import androidx.annotation.RequiresApi
import com.example.lockscreen1.extentions.CallManager

//import com.example.lockscreen1.ui.InComingCallReceiver

@RequiresApi(Build.VERSION_CODES.M)
class CallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        val intent = Intent(this, PhoneStateReceiver::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        CallManager.call = call
        CallManager.inCallService = this
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        CallManager.call = null
        CallManager.inCallService = null
    }
}