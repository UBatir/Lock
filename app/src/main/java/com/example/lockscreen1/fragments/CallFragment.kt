package com.example.lockscreen1.fragments

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Telephony
import android.telecom.Call
import android.telecom.TelecomManager
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.extentions.addCharacter
import com.example.lockscreen1.helpers.CallManager
import com.simplemobiletools.commons.extensions.*
import com.example.lockscreen1.extentions.config
import com.example.lockscreen1.extentions.getKeyEvent
import com.example.lockscreen1.helpers.SpeedDial
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.SimpleContact
import kotlinx.android.synthetic.main.call_fragment.*
import kotlinx.android.synthetic.main.dialpad.*
import kotlinx.android.synthetic.main.inline_call.*
import java.util.*


class CallFragment: Fragment(R.layout.call_fragment) {

    private var allContacts = ArrayList<SimpleContact>()
    private var speedDialValues = ArrayList<SpeedDial>()
    private var privateCursor: Cursor? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speedDialValues = context?.config!!.getSpeedDialValues()
        privateCursor = activity?.getMyContactsCursor()?.loadInBackground()

        dialpad_0_holder.setOnClickListener { dialpadPressed('0', it) }
        dialpad_1.setOnClickListener { dialpadPressed('1', it) }
        dialpad_2.setOnClickListener { dialpadPressed('2', it) }
        dialpad_3.setOnClickListener { dialpadPressed('3', it) }
        dialpad_4.setOnClickListener { dialpadPressed('4', it) }
        dialpad_5.setOnClickListener { dialpadPressed('5', it) }
        dialpad_6.setOnClickListener { dialpadPressed('6', it) }
        dialpad_7.setOnClickListener { dialpadPressed('7', it) }
        dialpad_8.setOnClickListener { dialpadPressed('8', it) }
        dialpad_9.setOnClickListener { dialpadPressed('9', it) }

        dialpad_1.setOnLongClickListener { speedDial(1); true }
        dialpad_2.setOnLongClickListener { speedDial(2); true }
        dialpad_3.setOnLongClickListener { speedDial(3); true }
        dialpad_4.setOnLongClickListener { speedDial(4); true }
        dialpad_5.setOnLongClickListener { speedDial(5); true }
        dialpad_6.setOnLongClickListener { speedDial(6); true }
        dialpad_7.setOnLongClickListener { speedDial(7); true }
        dialpad_8.setOnLongClickListener { speedDial(8); true }
        dialpad_9.setOnLongClickListener { speedDial(9); true }

        dialpad_0_holder.setOnLongClickListener { dialpadPressed('+', null); true }
        dialpad_asterisk.setOnClickListener { dialpadPressed('*', it) }
        dialpad_hashtag.setOnClickListener { dialpadPressed('#', it) }
        dialpad_clear_char.setOnClickListener { clearChar(it) }
        dialpad_clear_char.setOnLongClickListener { clearInput(); true }
        dialpad_call_button.setOnClickListener { initCall() }
        dialpad_input.onTextChangeListener { dialpadValueChanged(it) }
        SimpleContactsHelper(requireContext()).getAvailableContacts(false) { gotContacts(it) }
        disableKeyboardPopping()


    }
    override fun onResume() {
        super.onResume()
        dialpad_clear_char.applyColorFilter(context?.config!!.textColor)
    }


    private fun checkDialIntent(): Boolean {
        return if ((activity?.intent?.action == Intent.ACTION_DIAL || activity?.intent?.action == Intent.ACTION_VIEW) && activity?.intent?.data != null && activity?.intent?.dataString?.contains("tel:") == true) {
            val number = Uri.decode(activity?.intent?.dataString).substringAfter("tel:")
            dialpad_input.setText(number)
            dialpad_input.setSelection(number.length)
            true
        } else {
            false
        }
    }


    private fun dialpadPressed(char: Char, view: View?) {
        dialpad_input.addCharacter(char)
        view?.performHapticFeedback()
    }

    private fun clearChar(view: View) {
        dialpad_input.dispatchKeyEvent(dialpad_input.getKeyEvent(KeyEvent.KEYCODE_DEL))
        view.performHapticFeedback()
    }

    private fun clearInput() {
        dialpad_input.setText("")
    }

    private fun disableKeyboardPopping() {
        dialpad_input.showSoftInputOnFocus = false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            if (!isDefaultDialer()) {
                finish()
            } else {
                initOutgoingCall()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initOutgoingCall() {
        try {
            getHandleToUse(intent, callNumber.toString()) { handle ->
                Bundle().apply {
                    putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle)
                    putBoolean(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, false)
                    putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, false)
                    telecomManager.placeCall(callNumber, this)
                }
                finish()
            }
        } catch (e: Exception) {
            showErrorToast(e)
            finish()
        }
    }


    private fun initCall(number: String = dialpad_input.value) {
        if (number.isNotEmpty()) {
            startCallIntent(number)
        }
    }

    private fun speedDial(id: Int) {
        if (dialpad_input.value.isEmpty()) {
            val speedDial = speedDialValues.firstOrNull { it.id == id }
            if (speedDial?.isValid() == true) {
                initCall(speedDial.number)
            }
        }
    }
}
