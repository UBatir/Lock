package com.example.lockscreen1.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.telecom.TelecomManager
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.extentions.audioManager
import com.simplemobiletools.commons.extensions.getFormattedDuration
import com.simplemobiletools.commons.helpers.MINUTE_SECONDS
import kotlinx.android.synthetic.main.inline_call.*
import java.util.*

class InLineCall: Fragment(R.layout.inline_call) {

    private var privateCursor: Cursor? = null
    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var isSpeakerOn = false
    private var callTimer = Timer()
    private var isMicrophoneOn = true
    private var isCallEnded = false
    private var callDuration = 0
    private var callContact: ContactData? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProximitySensor()
        val number=arguments?.getString("number")
        caller_number_label.text=number
        callTimer.scheduleAtFixedRate(getCallTimerUpdateTask(), 1000, 1000)
        activity?.audioManager?.mode = AudioManager.MODE_IN_CALL
        CallManager.getCallContact(requireContext()){contact ->
            callContact = contact
        }

        call_end.setOnClickListener {
            if (proximityWakeLock?.isHeld == true) {
                proximityWakeLock!!.release()
            }
            endCall()
            val mFragment = CallFragment()
            val mBundle = Bundle()
            mFragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,mFragment)?.commit()
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun endCall(){
        val permissionCheck =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ANSWER_PHONE_CALLS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ANSWER_PHONE_CALLS),
                0
            )
        } else {
            val telecomManager = context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.endCall()
            return
        }

        isCallEnded = true
        if (callDuration > 0) {
            activity?.runOnUiThread {
                call_status_label.text = "${callDuration.getFormattedDuration()} (${getString(R.string.call_ended)})"
                Handler().postDelayed({
                    activity?.finish()
                    /*val mFragment = CallFragment()
                    val mBundle = Bundle()
                    mFragment.arguments = mBundle
                    activity?.supportFragmentManager?.beginTransaction()!!
                        .replace(R.id.fragment_container,mFragment).commit()*/
                }, 3000)
            }
        } else {
    //        call_status_label.text = getString(R.string.call_ended)
           // activity?.finish()
            /*val mFragment = CallFragment()
                    val mBundle = Bundle()
                    mFragment.arguments = mBundle
                    activity?.supportFragmentManager?.beginTransaction()!!
                        .replace(R.id.fragment_container,mFragment).commit()*/
        }

    }



    private fun getCallTimerUpdateTask() = object : TimerTask() {
        override fun run() {
            callDuration++
            activity?.runOnUiThread {
                if (!isCallEnded) {
    //                call_status_label.text = callDuration.getFormattedDuration()
                }
            }
        }
    }

    private fun initProximitySensor() {
        val powerManager = activity?.getSystemService(Context.POWER_SERVICE) as PowerManager
        proximityWakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "com.simplemobiletools.dialer.pro:wake_lock")
        proximityWakeLock!!.acquire(10 * MINUTE_SECONDS * 1000L)
    }


}