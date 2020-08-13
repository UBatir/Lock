package com.example.lockscreen1.extentions

import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import androidx.annotation.RequiresApi

class CallManager {
    companion object{
        var call: Call? = null
        var inCallService: InCallService? = null
        @RequiresApi(Build.VERSION_CODES.M)
        fun reject() {
            if (call != null) {
                if (call!!.state == Call.STATE_RINGING) {
                    call!!.reject(false, null)
                } else {
                    call!!.disconnect()
                }
            }
        }
        @RequiresApi(Build.VERSION_CODES.M)
        fun rejectCall(){
            call!!.disconnect()
        }

    }
}