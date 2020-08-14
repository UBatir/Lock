package com.example.lockscreen1.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.extentions.addCharacter
import com.example.lockscreen1.extentions.config
import com.example.lockscreen1.extentions.getAvailableSIMCardLabels
import com.example.lockscreen1.extentions.getKeyEvent
import com.example.lockscreen1.ui.LockScreenActivity
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.extensions.getPermissionString
import com.simplemobiletools.commons.extensions.hasPermission
import com.simplemobiletools.commons.extensions.performHapticFeedback
import com.simplemobiletools.commons.helpers.PERMISSION_READ_PHONE_STATE
import kotlinx.android.synthetic.main.call_fragment.*
import kotlinx.android.synthetic.main.dialpad.*


class CallFragment: Fragment(R.layout.call_fragment) {

    private var privateCursor: Cursor? = null
    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    var isAskingPermissions = false
    private val GENERIC_PERM_HANDLER = 100

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        dialpad_0_holder.setOnLongClickListener { dialpadPressed('+', null); true }
        dialpad_asterisk.setOnClickListener { dialpadPressed('*', it) }
        dialpad_hashtag.setOnClickListener { dialpadPressed('#', it) }
        dialpad_clear_char.setOnClickListener { clearChar(it) }
        dialpad_clear_char.setOnLongClickListener { clearInput(); true }
        dialpad_call_button.setOnClickListener {

            val number = dialpad_input.text.toString()
            if (number.isNotEmpty()) {
                startCall()
                val mFragment = InLineCall()
                val mBundle = Bundle()
                mBundle.putString("number", number)
                mFragment.arguments = mBundle
                activity?.supportFragmentManager?.beginTransaction()!!
                    .replace(R.id.fragment_container, mFragment).commit()
                Toast.makeText(
                    requireContext(),
                    "Идет набор на номер : $number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Заполните поле",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        disableKeyboardPopping()
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

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    fun startCall() {
                val number = dialpad_input.text.toString()
                if (number.isNotEmpty()) {
                    val telecomManager =
                        context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    val uri = Uri.fromParts("tel", number, null)
                    //   val extras = Bundle()
                    startInitCall(activity?.intent, number) {
                        Bundle().apply {
                            putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, it)
                            putBoolean(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, false)
                            putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, false)
                            telecomManager.placeCall(uri, this)
                        }
                    }
                }
            }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startInitCall(intent: Intent?, phoneNumber: String, callback: (handle: PhoneAccountHandle) -> Unit) {
        handlePermission(PERMISSION_READ_PHONE_STATE) { it ->
            if (it) {
                when {
                    intent?.hasExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE) == true -> callback(
                        intent.getParcelableExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE)!!
                    )
                    activity?.config?.getCustomSIM(phoneNumber)?.isNotEmpty() == true -> {
                        val storedLabel = Uri.decode(activity?.config?.getCustomSIM(phoneNumber))
                        val availableSIMs = activity?.getAvailableSIMCardLabels()
                        val firstornull =
                            availableSIMs?.firstOrNull { it.label == storedLabel }?.handle
                                ?: availableSIMs?.first()?.handle
                        callback(firstornull!!)
                    }
                    else -> {
                        SelectSIMDialog(
                            requireActivity() as LockScreenActivity,
                            phoneNumber
                        ) { handle ->
                            callback(handle)
                        }
                    }
                }
            }
        }
    }

    private fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (activity?.hasPermission(permissionId)!!) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(context?.getPermissionString(permissionId)), GENERIC_PERM_HANDLER)
        }
    }



}
