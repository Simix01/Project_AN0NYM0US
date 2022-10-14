package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val registrati : TextView = findViewById(R.id.loginActivity_registrati)
        val btn_login : Button = findViewById(R.id.loginActivity_Login)
        val email : EditText = findViewById(R.id.loginActivity_email)
        val password : EditText = findViewById(R.id.loginActivity_password)

        registrati.setOnClickListener(){
            val intent = Intent(this,RegisterActivity::class.java);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)

        }

        btn_login.setOnClickListener {
            when{
                TextUtils.isEmpty(email.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Inserisci email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Inserisci password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else ->{
                    val emailLogin : String = email.text.toString().trim{it <= ' '}
                    val pwLogin : String = password.text.toString().trim{it <= ' '}

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLogin, pwLogin).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            if(firebaseUser.isEmailVerified){
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Sei loggato correttamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", emailLogin)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(this, "La mail inserita non è ancora stata verificata", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } else{
                            Toast.makeText(
                                this@LoginActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}