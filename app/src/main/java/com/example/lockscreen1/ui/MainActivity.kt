package com.example.lockscreen1.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.lockscreen1.R
import com.example.lockscreen1.data.PasswordDatabase
import com.example.lockscreen1.data.dao.PasswordDao
import com.example.lockscreen1.data.model.Password
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var dao:PasswordDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        dao=PasswordDatabase.getInstance(this).dao()

        btnActivator.setOnClickListener {
            if (etPassword.text.isNotEmpty()){
                val a=etPassword.text.toString()
                addPassword(Password(1,a))
                Toast.makeText(this, "Вы успешно активировали программу", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LockScreenActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Заполните поле !!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addPassword(model:Password){
        dao.insertContact(model)
    }
}