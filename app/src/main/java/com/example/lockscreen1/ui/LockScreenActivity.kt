package com.example.lockscreen1.ui

import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.PasswordDatabase
import com.example.lockscreen1.data.dao.PasswordDao
import com.example.lockscreen1.dialog.CustomDialog
import com.example.lockscreen1.fragments.CallFragment
import com.example.lockscreen1.fragments.ContactFragment
import com.example.lockscreen1.fragments.MessageFragment
import com.example.lockscreen1.fragments.RingingFragment
import com.example.lockscreen1.interfaces.DestroyActivity
import com.example.lockscreen1.interfaces.SenderSms
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class LockScreenActivity : AppCompatActivity(),
    DestroyActivity, SenderSms{

    private val callFragment = CallFragment()
    private val smsFragment = MessageFragment(this)
    private val contactFragment = ContactFragment()
    lateinit var dao: PasswordDao
    var currentFocus = false
    var isPaused = false
    var collapseNotificationHandler: Handler? = null


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
        ActivityCompat.requestPermissions(this, arrayOf(SEND_SMS,
            READ_CONTACTS, READ_PHONE_STATE, CALL_PHONE, ANSWER_PHONE_CALLS, READ_CALL_LOG),1)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setSupportActionBar(toolbar)
        dao = PasswordDatabase.getInstance(this).dao()

        makeCurrentFragment(callFragment)
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.call -> makeCurrentFragment(callFragment)
                R.id.sms -> makeCurrentFragment(smsFragment)
                R.id.contacts -> makeCurrentFragment(contactFragment)
            }
            true
        }


        val call=intent.getBooleanExtra("InComingCall", false)
        if (call){
            val fragment = RingingFragment()
            val mBundle = Bundle()
            fragment.arguments = mBundle
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.pop_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> {
                val dialog =
                    CustomDialog(this, this)
                dialog.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
                .commit()

        }
    }


    override fun onBackPressed() {

    }

    override fun onPause() {
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
        super.onPause()
    }

    override fun destroyActivity() {
        finish()
        finishAffinity()
        onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        currentFocus = hasFocus
        if (!hasFocus) {
            collapseNow()
        }
    }

    private fun collapseNow() {
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = Handler()
        }

        if (!currentFocus && !isPaused) {

            collapseNotificationHandler!!.postDelayed(object : Runnable {
                @SuppressLint("WrongConstant")
                override fun run() {

                    // Use reflection to trigger a method from 'StatusBarManager'
                    val statusBarService = getSystemService("statusbar")
                    var statusBarManager: Class<*>? = null
                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager")
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                    var collapseStatusBar: Method? = null
                    try {

                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`
                        collapseStatusBar = if (Build.VERSION.SDK_INT > 16) {
                            statusBarManager!!.getMethod("collapsePanels")
                        } else {
                            statusBarManager!!.getMethod("collapse")
                        }
                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                    }
                    collapseStatusBar?.isAccessible = true
                    try {
                        collapseStatusBar?.invoke(statusBarService)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }

                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler!!.postDelayed(this, 100L)
                    }
                }
            }, 300L)
        }
    }

    override fun sendSms(number: String, text: String) {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, Manifest.permission_group.SMS::class.java), 0
        )
        val sms: SmsManager = SmsManager.getDefault()
        sms.sendTextMessage(number, null, text, pi, null)
        Toast.makeText(this, "отправлено", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            }
        }
    }
}


