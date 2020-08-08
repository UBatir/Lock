package com.example.lockscreen1.fragments

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.telecom.Call
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.extentions.addCharacter
import com.example.lockscreen1.helpers.CallManager
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.MINUTE_SECONDS
import com.simplemobiletools.commons.helpers.isOreoMr1Plus
import com.simplemobiletools.commons.helpers.isOreoPlus
import kotlinx.android.synthetic.main.call_fragment.*
import kotlinx.android.synthetic.main.inline_call.*
import java.util.*

class InLineCallFragment: Fragment(R.layout.inline_call) {
    private var isSpeakerOn = false
    private var isMicrophoneOn = true
    private var isCallEnded = false
    private var callDuration = 0
    private var callContact: ContactData? = null
    private var callContactAvatar: Bitmap? = null
    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var callTimer = Timer()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_IN_CALL


        CallManager.getCallContact(requireContext()) { contact ->
            callContact = contact

            activity?.runOnUiThread {
                updateOtherPersonsInfo()
                setupNotification()
                checkCalledSIMCard()
            }

        }
        updateCallState(CallManager.getState())
        addLockScreenFlags()
        initProximitySensor()

        CallManager.registerCallback(callCallback)
    }

    @SuppressLint("NewApi")
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            updateCallState(state)
        }
    }

    private fun initOutgoingCallUI() {
        incoming_call_holder.beGone()
        ongoing_call_holder.beVisible()
    }

    private fun updateCallState(state: Int) {
        when (state) {
            Call.STATE_RINGING -> callRinging()
            Call.STATE_ACTIVE -> callStarted()
            Call.STATE_DISCONNECTED -> endCall()
            Call.STATE_CONNECTING, Call.STATE_DIALING -> initOutgoingCallUI()
        }

        if (state == Call.STATE_DISCONNECTED || state == Call.STATE_DISCONNECTING) {
            callTimer.cancel()
        }

        val statusTextId = when (state) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DIALING -> R.string.dialing
            else -> 0
        }

        if (statusTextId != 0) {
            call_status_label.text = getString(statusTextId)
        }

    }
    private fun initButtons() {
        call_end.setOnClickListener {
            endCall()
        }
    }
    private fun endCall() {
        CallManager.reject()
        if (proximityWakeLock?.isHeld == true) {
            proximityWakeLock!!.release()
        }

        if (isCallEnded) {
            val fragment = CallFragment()
            val fBundle = Bundle()
            fragment.arguments = fBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)
            return
        }

        try {
        } catch (ignored: Exception) {
        }

        isCallEnded = true
        if (callDuration > 0) {
            activity?.runOnUiThread {
                Handler().postDelayed({
                    val fragment = CallFragment()
                    val fBundle = Bundle()
                    fragment.arguments = fBundle
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)
                }, 3000)
            }
        } else {
            call_status_label.text = getString(R.string.call_ended)
            val fragment = CallFragment()
            val fBundle = Bundle()
            fragment.arguments = fBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)
        }

//        btn_zero.setOnClickListener{ setTextFields('0')}
//        btn_zero.setOnLongClickListener {
//            setTextFields('+')
//            return@setOnLongClickListener true }
//        btn_num1.setOnClickListener{ setTextFields('1')}
//        btn_num2.setOnClickListener{ setTextFields('2')}
//        btn_num3.setOnClickListener{ setTextFields('3')}
//        btn_num4.setOnClickListener{ setTextFields('4')}
//        btn_num5.setOnClickListener{ setTextFields('5')}
//        btn_num6.setOnClickListener{ setTextFields('6')}
//        btn_num7.setOnClickListener{ setTextFields('7')}
//        btn_num8.setOnClickListener{ setTextFields('8')}
//        btn_num9.setOnClickListener{ setTextFields('9')}
//        btn_star.setOnClickListener { setTextFields('*') }
//        btn_reshetka.setOnClickListener { setTextFields('#') }
//
//

    }
//    private fun setTextFields(char: Char) {
//        CallManager.keypad(char)
////        tvEnter.addCharacter(char)
//        tvEnter.append(char.toString())
//    }

    private fun callRinging() {
        incoming_call_holder.beVisible()
    }

    private fun callStarted() {
        incoming_call_holder.beGone()
        ongoing_call_holder.beVisible()
        try {
            callTimer.scheduleAtFixedRate(getCallTimerUpdateTask(), 1000, 1000)
        } catch (ignored: Exception) {
        }
    }
    private fun initProximitySensor() {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        proximityWakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "com.simplemobiletools.dialer.pro:wake_lock")
        proximityWakeLock!!.acquire(10 * MINUTE_SECONDS * 1000L)
    }

    private fun getCallTimerUpdateTask() = object : TimerTask() {
        override fun run() {
            callDuration++
            activity?.runOnUiThread {
                if (!isCallEnded) {
                    call_status_label.text = callDuration.getFormattedDuration()
                }
            }
        }
    }


    @SuppressLint("NewApi")
    private fun addLockScreenFlags() {
        if (isOreoMr1Plus()) {
            activity?.setShowWhenLocked(true)
            activity?.setTurnScreenOn(true)
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }

        if (isOreoPlus()) {
            (context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).requestDismissKeyguard(requireActivity(), null)
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }

    @SuppressLint("NewApi")
    private fun setupNotification() {
        val callState = CallManager.getState()
        val channelId = "simple_dialer_call"
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val name = "call_notification_channel"

            NotificationChannel(channelId, name, importance).apply {
                setSound(null, null)
                activity?.notificationManager?.createNotificationChannel(this)
            }
        }

        val callerName = if (callContact != null && callContact!!.name.isNotEmpty()) callContact!!.name else getString(R.string.unknown_caller)
        val contentTextId = when (callState) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DIALING -> R.string.dialing
            Call.STATE_DISCONNECTED -> R.string.call_ended
            Call.STATE_DISCONNECTING -> R.string.call_ending
            else -> R.string.ongoing_call
        }

//        val collapsedView = RemoteViews(activity?.packageName, R.layout.call_notification).apply {
//            setText(R.id.notification_caller_name, callerName)
//            setText(R.id.notification_call_status, getString(contentTextId))
//            setVisibleIf(R.id.notification_accept_call, callState == Call.STATE_RINGING)

//            setOnClickPendingIntent(R.id.notification_decline_call, declinePendingIntent)
//            setOnClickPendingIntent(R.id.notification_accept_call, acceptPendingIntent)



        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_phone_vector)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_CALL)
            .setOngoing(true)
            .setSound(null)
            .setUsesChronometer(callState == Call.STATE_ACTIVE)
            .setChannelId(channelId)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        val notification = builder.build()
        activity?.notificationManager?.notify(1, notification)
    }
    private fun updateOtherPersonsInfo() {
        if (callContact == null) {
            return
        }

        caller_name_label.text = if (callContact!!.name.isNotEmpty()) callContact!!.name else getString(R.string.unknown_caller)
        if (callContact!!.number.isNotEmpty() && callContact!!.number != callContact!!.name) {
            caller_number_label.text = callContact!!.number
        } else {
            caller_number_label.beGone()
        }

        if (callContactAvatar != null) {
            caller_avatar.setImageBitmap(callContactAvatar)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun checkCalledSIMCard() {
        try {
            val accounts = activity?.telecomManager?.callCapablePhoneAccounts
            if (accounts?.size!! > 1) {
                accounts.forEachIndexed { index, account ->
                    if (account == CallManager.call?.details?.accountHandle) {
                        call_sim_id.text = "${index + 1}"
                        call_sim_id.beVisible()
                        call_sim_image.beVisible()
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    }


