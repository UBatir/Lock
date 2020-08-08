package com.example.lockscreen1.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import androidx.annotation.RequiresApi
import com.example.lockscreen1.data.ContactData
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.helpers.MyContactsContentProvider
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import com.simplemobiletools.commons.helpers.ensureBackgroundThread

@SuppressLint("NewApi")
class CallManager {

    companion object {
        var call: Call? = null
        var inCallService: InCallService? = null

        @RequiresApi(Build.VERSION_CODES.M)
        fun accept() {
            call?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        fun reject() {
            if (call != null) {
                if (call!!.state == Call.STATE_RINGING) {
                    call!!.reject(false, null)
                } else {
                    call!!.disconnect()
                }
            }
        }

        fun registerCallback(callback: Call.Callback) {
            if (call != null) {
                call!!.registerCallback(callback)
            }
        }

        fun unregisterCallback(callback: Call.Callback) {
            call?.unregisterCallback(callback)
        }

        fun getState() = if (call == null) {
            Call.STATE_DISCONNECTED
        } else {
            call!!.state
        }


        fun keypad(c: Char) {
            call?.playDtmfTone(c)
            call?.stopDtmfTone()
        }


        fun getCallContact(context: Context, callback: (ContactData?) -> Unit) {
            val callContact = ContactData("", "")
            if (CallManager.call == null || CallManager.call!!.details == null || CallManager.call!!.details!!.handle == null) {
                callback(callContact)
                return
            }
            val uri = Uri.decode(CallManager.call!!.details.handle.toString())
            if (uri.startsWith("tel:")) {
                val number = uri.substringAfter("tel:")
                callContact.number = number
                callContact.name = SimpleContactsHelper(context).getNameFromPhoneNumber(number)

                if (callContact.name != callContact.number) {
                    callback(callContact)
                } else {
                    val privateCursor = context.getMyContactsCursor().loadInBackground()
                    ensureBackgroundThread {
                        val privateContacts =
                            MyContactsContentProvider.getSimpleContacts(context, privateCursor)
                        val privateContact =
                            privateContacts.firstOrNull { it.phoneNumbers.first() == callContact.number }
                        if (privateContact != null) {
                            callContact.name = privateContact.name
                        }
                        callback(callContact)
                    }
                }
            }

        }
    }
}

