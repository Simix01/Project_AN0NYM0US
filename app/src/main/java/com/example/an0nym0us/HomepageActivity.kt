package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        inizializzaBottomMenu()
    }

    fun inizializzaBottomMenu(){
        var bottomNavigationMenuView : BottomNavigationView = findViewById(R.id.bottom_home)
        bottomNavigationMenuView.setSelectedItemId(R.id.homepage)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.explore -> {
                    val intent =
                        Intent(this@HomepageActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@HomepageActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@HomepageActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@HomepageActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
            }
            true
        }
    }
}