package com.example.lockscreen1.fragments

import android.os.Build
import android.telecom.PhoneAccountHandle
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.lockscreen1.R
import com.example.lockscreen1.extentions.config
import com.example.lockscreen1.extentions.getAvailableSIMCardLabels
import com.example.lockscreen1.ui.LockScreenActivity
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_select_sim.view.*


@RequiresApi(Build.VERSION_CODES.M)
class SelectSIMDialog(val activity: LockScreenActivity, val phoneNumber: String, val callback: (handle: PhoneAccountHandle) -> Unit){

    private var dialog: AlertDialog? = null
    private val view = activity.layoutInflater.inflate(R.layout.dialog_select_sim, null)

    init {
        val radioGroup = view.select_sim_radio_group

        activity.getAvailableSIMCardLabels().forEachIndexed { index, SIMAccount ->
            val radioButton = (activity.layoutInflater.inflate(R.layout.radio_button, null) as RadioButton).apply {
                text = SIMAccount.label
                id = index
                setOnClickListener { selectedSIM(SIMAccount.handle, SIMAccount.label) }
            }
            radioGroup!!.addView(radioButton, RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }

        dialog = AlertDialog.Builder(activity)
            .create().apply {
                activity.setupDialogStuff(view, this)
            }
    }

    private fun selectedSIM(handle: PhoneAccountHandle, label: String) {
        if (view.select_sim_remember.isChecked) {
            activity.config.saveCustomSIM(phoneNumber, label)
        }

        callback(handle)
        dialog?.dismiss()
    }

}
