package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        inizializzaBottomMenu()
        inizializzaGridView()

    }

    fun inizializzaBottomMenu(){
        var bottomNavigationMenuView : BottomNavigationView = findViewById(R.id.bottom_profile)
        bottomNavigationMenuView.setSelectedItemId(R.id.profile)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@ProfileActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.explore -> {
                    val intent =
                        Intent(this@ProfileActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@ProfileActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@ProfileActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
            }
            true
        }
    }

    fun inizializzaGridView(){
        lateinit var postGRV: GridView
        lateinit var postList: List<GridViewModal>

        postGRV = findViewById(R.id.grid_post)
        postList = ArrayList<GridViewModal>()

        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)
        postList = postList + GridViewModal(R.drawable.giga)


        val postAdapter = GridAdapter(postList = postList, this@ProfileActivity)

        postGRV.adapter = postAdapter

    }
}