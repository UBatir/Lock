package com.example.lockscreen1.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.lockscreen1.dialog.InComingDialog

open class InCommingCallReceiver(): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        try{
            val state: String? = p1?.getStringExtra(TelephonyManager.EXTRA_STATE)

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    val dialog = InComingDialog(p0!!)
                dialog.show()
                //Toast.makeText(p0, "Phone Is Ringing", Toast.LENGTH_LONG).show();
            }

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(p0, "Call Recieved", Toast.LENGTH_LONG).show();
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(p0, "Phone Is Idle", Toast.LENGTH_LONG).show();
            }
        }
        catch(e: Exception ){e.printStackTrace();}
    }
}