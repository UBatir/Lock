package com.example.lockscreen1.extentions

import android.content.Context
import android.net.Uri
import android.os.Build
import android.telecom.Call
import androidx.annotation.RequiresApi
import com.example.lockscreen1.data.ContactData
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.helpers.MyContactsContentProvider
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import com.simplemobiletools.commons.helpers.ensureBackgroundThread

class CallManager {
    companion object{
        var call: Call? = null

        @RequiresApi(Build.VERSION_CODES.M)
        fun getCallContact(context: Context, callback: (ContactData?) -> Unit) {
            val callContact = ContactData("", "")
            if (call == null || call!!.details == null || call!!.details!!.handle == null) {
                callback(callContact)
                return
            }

            val uri = Uri.decode(call!!.details.handle.toString())
            if (uri.startsWith("tel:")) {
                val number = uri.substringAfter("tel:")
                callContact.number = number
                callContact.name = SimpleContactsHelper(context).getNameFromPhoneNumber(number)
                if (callContact.name != callContact.number) {
                    callback(callContact)
                } else {
                    val privateCursor = context.getMyContactsCursor().loadInBackground()
                    ensureBackgroundThread {
                        val privateContacts = MyContactsContentProvider.getSimpleContacts(context, privateCursor)
                        val privateContact = privateContacts.firstOrNull { it.phoneNumbers.first() == callContact.number }
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

