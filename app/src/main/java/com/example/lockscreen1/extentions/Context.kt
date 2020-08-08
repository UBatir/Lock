package com.example.lockscreen1.extentions

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import com.example.lockscreen1.helpers.Config

val Context.config: Config get() = Config.newInstance(applicationContext)

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("MissingPermission")
fun Context.getAvailableSIMCardLabels(): ArrayList<SIMAccount> {
    val SIMAccounts = ArrayList<SIMAccount>()
    try {
        val TelecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        TelecomManager.callCapablePhoneAccounts.forEachIndexed { index, account ->
            val phoneAccount = TelecomManager.getPhoneAccount(account)
            var label = phoneAccount.label.toString()
            var address = phoneAccount.address.toString()
            if (address.startsWith("tel:") && address.substringAfter("tel:").isNotEmpty()) {
                address = Uri.decode(address.substringAfter("tel:"))
                label += " ($address)"
            }

            val SIM = SIMAccount(index + 1, phoneAccount.accountHandle, label, address.substringAfter("tel:"))
            SIMAccounts.add(SIM)
        }
    } catch (ignored: Exception) {
    }
    return SIMAccounts
}
