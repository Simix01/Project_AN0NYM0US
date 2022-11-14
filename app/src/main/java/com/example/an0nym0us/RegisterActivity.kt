package com.example.an0nym0us

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlin.math.absoluteValue

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val btn_register : Button = findViewById(R.id.registerActivity_Registrati)
        val email : EditText = findViewById(R.id.registerActivity_email)
        val password : EditText = findViewById(R.id.registerActivity_password)
        val confirm_pw : EditText = findViewById(R.id.registerActivity_passwordRipeti)

        btn_register.setOnClickListener{
            when{
                TextUtils.isEmpty(email.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Inserisci email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Inserisci password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !TextUtils.equals(password.text.toString().trim{it <= ' '}, confirm_pw.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Le password inserite non coincidono",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val emailInserita : String = email.text.toString().trim{it <= ' '}
                    val passwordInserita : String = password.text.toString().trim{it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailInserita, passwordInserita).addOnCompleteListener(
                        OnCompleteListener<AuthResult>{ task ->
                            if(task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                firebaseUser.sendEmailVerification().addOnSuccessListener {
                                    Toast.makeText(this, "Verifica la tua mail cliccando il link di verifica sulla mail"
                                        ,Toast.LENGTH_SHORT).show()
                                }

                                val cUser = FirebaseAuth.getInstance().currentUser!!.uid
                                val valoreHash = cUser.hashCode().absoluteValue
                                val uId = "anonym$valoreHash"
                                var database = FirebaseDatabase
                                    .getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("InfoUtenti/")

                                val proPic = "ok"
                                val nickname = uId
                                val approvazioni = 0
                                val canEdit = false
                                val canBeFound = false
                                var seguiti = arrayListOf<String>()
                                seguiti.add("ok")

                                val utente = Utente(proPic,nickname,approvazioni,canEdit,canBeFound, seguiti)
                                database.child(nickname).setValue(utente).addOnSuccessListener {
                                    Toast.makeText(this,"evviva", Toast.LENGTH_SHORT)
                                }.addOnFailureListener {
                                    Toast.makeText(this,"evviva ma in rosso", Toast.LENGTH_SHORT)
                                }

                                val intent =
                                    Intent(this@RegisterActivity, LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", emailInserita)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }


        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

}