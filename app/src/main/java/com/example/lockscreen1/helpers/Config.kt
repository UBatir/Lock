package com.example.lockscreen1.helpers
import android.content.Context
import android.net.Uri
import com.simplemobiletools.commons.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {

    companion object {
        const val REMEMBER_SIM_PREFIX = "remember_sim_"
        const val GROUP_SUBSEQUENT_CALLS = "group_subsequent_calls"
        fun newInstance(context: Context) = Config(context)
    }
    fun saveCustomSIM(number: String, SIMlabel: String) {
        prefs.edit().putString(REMEMBER_SIM_PREFIX + number, Uri.encode(SIMlabel)).apply()
    }

    fun getCustomSIM(number: String) = prefs.getString(REMEMBER_SIM_PREFIX + number, "")

    var groupSubsequentCalls: Boolean
        get() = prefs.getBoolean(GROUP_SUBSEQUENT_CALLS, true)
        set(groupSubsequentCalls) = prefs.edit().putBoolean(GROUP_SUBSEQUENT_CALLS, groupSubsequentCalls).apply()


}
