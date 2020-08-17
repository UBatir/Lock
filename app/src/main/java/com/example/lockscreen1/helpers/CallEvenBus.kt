package com.example.lockscreen1.helpers

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CallEvenBus {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CallEvenBus?) {

    }
}