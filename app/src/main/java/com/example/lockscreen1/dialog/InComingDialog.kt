package com.example.lockscreen1.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.lockscreen1.R
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.fragments.InLineCall
import com.example.lockscreen1.ui.LockScreenActivity
import kotlinx.android.synthetic.main.call_notification.*

class InComingDialog(context: Context): Dialog(context) {
    private val activity = LockScreenActivity()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_notification)
        notification_decline_call.setOnClickListener {
            CallManager.reject()
            dismiss()
        }
        notification_accept_call.setOnClickListener {
            CallManager.accept()
            val fragment = InLineCall()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        }
    }
}