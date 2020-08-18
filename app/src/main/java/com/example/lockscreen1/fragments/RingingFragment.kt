package com.example.lockscreen1.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import kotlinx.android.synthetic.main.ringing_fragment.*


class RingingFragment: Fragment(R.layout.ringing_fragment) {

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
                caller_name_label.text=contactName
                cur.close()
                return true
            }
        }
        caller_name_label.text="Неизвестный номер"
        return false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phoneNumber = activity!!.intent.getStringExtra("inComingNumber")
        caller_number_label.text =  phoneNumber
        contactExists(context!!,phoneNumber)
        val a=activity!!.intent.getIntExtra("InComingCall", 0)
        if(a==1){
            activity!!.finish()
        }



        call_decline.setOnClickListener {
            endCall()
            activity!!.finish()
        }
        call_accept.setOnClickListener {
            acceptCall()
            val fragment = InLineCall()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                fragment
            )?.commit()

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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun acceptCall() {
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
            telecomManager.acceptRingingCall()
            return
        }

    }

}