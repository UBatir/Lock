package com.example.lockscreen1.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.extentions.CallManager
import com.example.lockscreen1.extentions.addCharacter
import com.example.lockscreen1.extentions.getKeyEvent
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.extensions.performHapticFeedback
import kotlinx.android.synthetic.main.call_fragment.*
import kotlinx.android.synthetic.main.dialpad.*


class CallFragment: Fragment(R.layout.call_fragment) {

    private var privateCursor: Cursor? = null
    private val REQUEST_CALL = 1
    private val REQUEST_READ_PHONE_STATE = 1

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
        btnEndCall.setOnClickListener {
          //  endCall2()
            rejectCall()
        }
        dialpad_clear_char.setOnLongClickListener { clearInput(); true }
        dialpad_call_button.setOnClickListener {
            val number = dialpad_input.text.toString()
            startCall()
            val fragment = InLineCall()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()!!
                .replace(R.id.fragment_container,fragment)
            Toast.makeText(requireContext(),"Идет набор на номер : $number", Toast.LENGTH_SHORT).show()
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


    private fun makePhoneCall() {
        val number = dialpad_input.text.toString()
        if (number.trim { it <= ' ' }.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
            } else {
                val a = Uri.encode(number)
                val dial = "tel:$a"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
                Toast.makeText(requireContext(),"Идет набор на номер ",Toast.LENGTH_LONG).show()

            }
        } else {
            Toast.makeText(requireContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(requireContext(), "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startCall(){
        val number = dialpad_input.text.toString()
        val telecomManager = context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val uri = Uri.fromParts("tel", number, null)
        val extras = Bundle()
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                activity!!.packageName
            )
            startActivity(intent)
            return
        }
        telecomManager.placeCall(uri, extras)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun endCall(){
        val permissionCheck =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ANSWER_PHONE_CALLS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ANSWER_PHONE_CALLS),
                0
            )
        } else {
            val telecomManager = context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.endCall()
            return
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun endCall2(){
        val number = ""
        val telecomManager = context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val uri = Uri.fromParts("tel", number, null)
        val extras = Bundle()
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                activity!!.packageName
            )
            startActivity(intent)
            return
        }
        telecomManager.placeCall(uri, extras)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun rejectCall(){
        CallManager.call!!.disconnect()

    }
}
