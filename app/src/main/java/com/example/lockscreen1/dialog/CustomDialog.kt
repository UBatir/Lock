package com.example.lockscreen1.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import com.example.lockscreen1.R
import com.example.lockscreen1.data.PasswordDatabase
import com.example.lockscreen1.data.dao.PasswordDao
import com.example.lockscreen1.interfaces.DestroyActivity
import com.example.lockscreen1.ui.LockScreenActivity
import kotlinx.android.synthetic.main.custom.*


class CustomDialog(private val activity: LockScreenActivity, private val listener: DestroyActivity) : Dialog(activity) {

    lateinit var dao:PasswordDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom)
        dao=PasswordDatabase.getInstance(activity).dao()
        val a=dao.getPassword()
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        btnPositive.setOnClickListener {
            if(etPasswordDeActivator.text.toString()==a.key){
                listener.destroyActivity()
                dao.deletePassword(a)
            }else{
                etPasswordDeActivator.text.clear()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v?.vibrate(100);
                }
                Toast.makeText(context,"Вы не правильно ввели пароль!",Toast.LENGTH_LONG).show()
            }
        }
    }
}