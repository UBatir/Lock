package com.example.lockscreen1.extentions

import android.annotation.SuppressLint
import android.content.Intent
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import com.simplemobiletools.commons.helpers.PERMISSION_READ_PHONE_STATE
import com.simplemobiletools.commons.extensions.isDefaultDialer
import com.simplemobiletools.commons.extensions.launchCallIntent
import com.simplemobiletools.commons.extensions.telecomManager

fun startCallIntent(recipient: String) {
    if (isDefaultDialer()) {
        getHandleToUse(null, recipient) { handle ->
            launchCallIntent(recipient, handle)
        }
    } else {
        launchCallIntent(recipient, null)
    }
}

// used at devices with multiple SIM cards
@SuppressLint("MissingPermission")
fun getHandleToUse(intent: Intent?, phoneNumber: String, callback: (handle: PhoneAccountHandle) -> Unit) {
    handlePermission(PERMISSION_READ_PHONE_STATE) {
        if (it) {
            val defaultHandle = telecomManager.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)
            when {
                intent?.hasExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE) == true -> callback(intent.getParcelableExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE)!!)
                config.getCustomSIM(phoneNumber)?.isNotEmpty() == true -> {
                    val storedLabel = Uri.decode(config.getCustomSIM(phoneNumber))
                    val availableSIMs = getAvailableSIMCardLabels()
                    val firstornull = availableSIMs.firstOrNull { it.label == storedLabel }?.handle ?: availableSIMs.first().handle
                    callback(firstornull)
                }
                defaultHandle != null -> callback(defaultHandle)
                else -> {
                    SelectSIMDialog(this, phoneNumber) { handle ->
                        callback(handle)
                    }
                }
            }
        }
    }
}
