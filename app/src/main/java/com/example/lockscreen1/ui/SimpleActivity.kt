package com.example.lockscreen1.ui

import com.example.lockscreen1.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import java.util.*

class SimpleActivity : BaseSimpleActivity() {
    override fun getAppIconIDs(): ArrayList<Int> {
        TODO("Not yet implemented")
    }

    override fun getAppLauncherName(): String = getString(R.string.app_launcher_name)
}