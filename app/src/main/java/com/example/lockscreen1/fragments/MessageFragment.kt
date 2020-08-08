package com.example.lockscreen1.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.interfaces.SenderSms
import kotlinx.android.synthetic.main.message_fragment.*


open class MessageFragment(private val listener: SenderSms) : Fragment(R.layout.message_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnSendSms.setOnClickListener {
            val number = etKomu.text.toString()
            val sms = etSendSms.text.toString()
            if (number.isNotEmpty() && sms.isNotEmpty()){
                listener.sendSms(number, sms)
                etKomu.text.clear()
                etSendSms.text.clear()
            }else{
                Toast.makeText(requireContext(), " Заполните поля ! " , Toast.LENGTH_SHORT).show()
            }
        }

    }

}


