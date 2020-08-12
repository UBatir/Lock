package com.example.lockscreen1.data

import android.telecom.PhoneAccountHandle

data class SimData(val id: Int, val handle: PhoneAccountHandle, val label: String, val phoneNumber: String)