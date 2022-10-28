package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.absoluteValue

class NotificationsActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val uId = cUser.hashCode().absoluteValue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        inizializzaBottomMenu()

    }

    fun inizializzaBottomMenu(){
        var bottomNavigationMenuView : BottomNavigationView = findViewById(R.id.bottom_notifications)
        bottomNavigationMenuView.setSelectedItemId(R.id.notifications)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@NotificationsActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.explore -> {
                    val intent =
                        Intent(this@NotificationsActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@NotificationsActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@NotificationsActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
            }
            true
        }
    }
}