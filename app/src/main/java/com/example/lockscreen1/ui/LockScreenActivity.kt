package com.example.lockscreen1.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.data.PasswordDatabase
import com.example.lockscreen1.data.dao.PasswordDao
import com.example.lockscreen1.dialog.CustomDialog
import com.example.lockscreen1.fragments.CallFragment
import com.example.lockscreen1.fragments.ContactFragment
import com.example.lockscreen1.fragments.InLineCallFragment
import com.example.lockscreen1.fragments.MessageFragment
import com.example.lockscreen1.interfaces.DestroyActivity
import com.example.lockscreen1.interfaces.SenderSms
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class LockScreenActivity : AppCompatActivity(),
    DestroyActivity, SenderSms {
    private val callFragment = CallFragment()
    private val inLineCallFragment = InLineCallFragment()
    private val smsFragment = MessageFragment(this)
    private val contactFragment = ContactFragment()
    lateinit var dao: PasswordDao
    var currentFocus = false

    // To keep track of activity's foreground/background status
    var isPaused = false

    var collapseNotificationHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
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
        // Initialize 'collapseNotificationHandler'

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = Handler()
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
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

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
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



}