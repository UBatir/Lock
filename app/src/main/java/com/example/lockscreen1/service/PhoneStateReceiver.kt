package com.example.lockscreen1.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.lockscreen1.ui.LockScreenActivity


open  class PhoneStateReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val extras = intent.extras
        if (extras != null) {
            val state = extras.getString(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val phoneNumber = extras
                    .getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val isCalling = true
                val i = Intent(context, LockScreenActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("InComingCall", isCalling)
                i.putExtra("number", phoneNumber)
                Toast.makeText(context, phoneNumber, Toast.LENGTH_LONG).show()
                context.startActivity(i)
            }
            if(state==TelephonyManager.EXTRA_STATE_IDLE){
                Toast.makeText(context,"Zvanok jabildi", Toast.LENGTH_LONG).show()
                val phoneNumber = extras
                    .getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val a = 1
                val i = Intent(context, LockScreenActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("InComingCall", a)
                i.putExtra("number", phoneNumber)
                Toast.makeText(context, phoneNumber, Toast.LENGTH_LONG).show()
                context.startActivity(i)
            }
        }
    }
}


