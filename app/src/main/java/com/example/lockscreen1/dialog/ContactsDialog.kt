package com.example.lockscreen1.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.lockscreen1.R

class ContactsDialog(context: Context):Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_contact)
    }
}