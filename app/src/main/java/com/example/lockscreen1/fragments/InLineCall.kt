package com.example.lockscreen1.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.TelecomManager
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.extentions.audioManager
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.helpers.MINUTE_SECONDS
import kotlinx.android.synthetic.main.inline_call.*

class InLineCall: Fragment(R.layout.inline_call) {

    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var callContact: ContactData? = null
    var number: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProximitySensor()


        val ring = activity!!.intent.getBooleanExtra("InComingCall", false)
        if (ring){
            val phoneNumber = activity!!.intent.getStringExtra("inComingNumber")
            caller_number_label.text =  phoneNumber
            contactExists(context!!,phoneNumber)

        }else{

            number = arguments?.getString("number")
            caller_number_label.text =  number
            contactExists(context!!,number)
        }



        activity?.audioManager?.mode = AudioManager.MODE_IN_CALL
        CallManager.getCallContact(requireContext()){contact ->
            callContact = contact

        }
        CallManager.registerCallback(callCallback)
        updateCallState(CallManager.getState())

        call_end.setOnClickListener {
            if (proximityWakeLock?.isHeld == true) {
                proximityWakeLock!!.release()
            }
            endCall()
            val fragment =CallFragment()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                fragment
            )?.commit()
        }
    }

    private fun contactExists(context: Context, number: String?): Boolean {
        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val mPhoneNumberProjection =
            arrayOf(ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cur: Cursor? =
            context.contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)
        cur.use { cur ->
            if (cur!!.moveToFirst()) {
                val contactName = cur.getString(cur
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                caller_name.text=contactName
                cur.close()
                return true
            }
        }
        caller_name.text="Неизвестный номер"
        return false
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