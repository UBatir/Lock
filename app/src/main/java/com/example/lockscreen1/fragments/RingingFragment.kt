package com.example.lockscreen1.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.extentions.CallManager
import kotlinx.android.synthetic.main.ringing_fragment.*

class RingingFragment: Fragment(R.layout.ringing_fragment) {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        call_decline.setOnClickListener {
            CallManager.reject()
            val fFragment = CallFragment()
            val mBundle = Bundle()
            fFragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fFragment)
        }
        call_accept.setOnClickListener {
            CallManager.accept()
            val fragment = InLineCall()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)

        }
    }
}