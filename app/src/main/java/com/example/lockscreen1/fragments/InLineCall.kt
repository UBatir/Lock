package com.example.lockscreen1.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telecom.Call
import android.telecom.TelecomManager
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.extentions.ACCEPT_CALL
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.extentions.DECLINE_CALL
import com.example.lockscreen1.extentions.audioManager
import com.example.lockscreen1.ui.InCommingCallReceiver
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.notificationManager
import com.simplemobiletools.commons.helpers.MINUTE_SECONDS
import com.simplemobiletools.commons.helpers.isOreoPlus
import kotlinx.android.synthetic.main.inline_call.*

class InLineCall: Fragment(R.layout.inline_call) {

    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var callContact: ContactData? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProximitySensor()
        initButtons()
        val number=arguments?.getString("number")
        caller_number_label.text=number
        activity?.audioManager?.mode = AudioManager.MODE_IN_CALL
        CallManager.getCallContact(requireContext()){contact ->
            callContact = contact
            activity?.runOnUiThread {
                setupNotification()
            }
        }
        CallManager.registerCallback(callCallback)
        updateCallState(CallManager.getState())
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initButtons() {
        call_decline.setOnClickListener {
            endCall()
        }

        call_accept.setOnClickListener {
            acceptCall()
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun endCall() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ANSWER_PHONE_CALLS
            )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ANSWER_PHONE_CALLS),
                0
            )
        } else {
            val telecomManager =
                context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.endCall()
            return
        }

    }

    private fun initProximitySensor() {
        val powerManager = activity?.getSystemService(Context.POWER_SERVICE) as PowerManager
        proximityWakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "com.simplemobiletools.dialer.pro:wake_lock")
        proximityWakeLock!!.acquire(10 * MINUTE_SECONDS * 1000L)
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

        val openAppIntent = Intent(requireContext(), InLineCall::class.java)
        openAppIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        val openAppPendingIntent = PendingIntent.getActivity(requireContext(), 0, openAppIntent, 0)

        val acceptCallIntent = Intent(requireContext(), InCommingCallReceiver::class.java)
        acceptCallIntent.action = ACCEPT_CALL
        val acceptPendingIntent = PendingIntent.getBroadcast(requireContext(), 0, acceptCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val declineCallIntent = Intent(requireContext(), InCommingCallReceiver::class.java)
        declineCallIntent.action = DECLINE_CALL
        val declinePendingIntent = PendingIntent.getBroadcast(requireContext(), 1, declineCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val callerName = if (callContact != null && callContact!!.name.isNotEmpty()) callContact!!.name else getString(R.string.unknown_caller)
        val contentTextId = when (callState) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DIALING -> R.string.dialing
            Call.STATE_DISCONNECTED -> R.string.call_ended
            Call.STATE_DISCONNECTING -> R.string.call_ending
            else -> R.string.ongoing_call
        }

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_phone_vector)
            .setContentIntent(openAppPendingIntent)
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

    private fun updateCallState(state: Int) {
        when (state) {
            Call.STATE_RINGING ->{callRinging()
                Toast.makeText(requireContext(), "Ringing?", Toast.LENGTH_SHORT).show()

            }
            Call.STATE_ACTIVE -> {callStarted()
                Toast.makeText(requireContext(), "start?", Toast.LENGTH_SHORT).show()

            }
            Call.STATE_DISCONNECTED -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Toast.makeText(requireContext(), "end", Toast.LENGTH_SHORT).show()

                endCall()
            }
            Call.STATE_CONNECTING, Call.STATE_DIALING -> initOutgoingCallUI()
        }


        val statusTextId = when (state) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DIALING -> R.string.dialing
            else -> 0
        }

        if (statusTextId != 0) {
            call_status_label.text = getString(statusTextId)
        }

        setupNotification()

    }
    private fun callStarted() {
        incoming_call_holder.beGone()
        ongoing_call_holder.beVisible()
    }


    private fun acceptCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CallManager.accept()
        }
    }

    private fun initOutgoingCallUI() {
        incoming_call_holder.beGone()
        ongoing_call_holder.beVisible()
    }

    private fun callRinging() {
        incoming_call_holder.beVisible()
    }

    @SuppressLint("NewApi")
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            updateCallState(state)
        }
    }

}