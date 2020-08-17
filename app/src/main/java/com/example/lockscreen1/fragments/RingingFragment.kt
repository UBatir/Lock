package com.example.lockscreen1.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import kotlinx.android.synthetic.main.ringing_fragment.*

class RingingFragment: Fragment(R.layout.ringing_fragment) {
    var callContact: ContactData? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val phoneNumber = arguments!!.getString("number")
//        caller_number_label.text =  phoneNumber

//        if (phoneNumber.isNullOrEmpty() && phoneNumber == callContact!!.number ){
//            caller_name_label.text = callContact!!.name
//        }else{
//            caller_name_label.text = "Неизвестный номер"
//        }


        call_decline.setOnClickListener {
            endCall()
            val fFragment = CallFragment()
            val mBundle = Bundle()
            fFragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fFragment)?.commit()
        }
        call_accept.setOnClickListener {
            acceptCall()
            val fragment = InLineCall()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)?.commit()

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