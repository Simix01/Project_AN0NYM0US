package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val registrati : TextView = findViewById(R.id.loginActivity_registrati)

        registrati.setOnClickListener(){
            val intent = Intent(this,RegisterActivity::class.java);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)

        }
    }
}