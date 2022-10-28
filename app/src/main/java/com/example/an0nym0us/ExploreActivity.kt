package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_explore.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.math.absoluteValue

class ExploreActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val uId = cUser.hashCode().absoluteValue
    private lateinit var postAdapter: PostRecyclerAdapterGrid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        inizializzaBottomMenu()
        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet() {
        val data = DataSource.createDataSet()
        postAdapter.submitList(data)
    }

    private fun initRecyclerView() {
        StaggeredGrid_Explore.layoutManager = StaggeredGridLayoutManager(3,LinearLayoutManager.VERTICAL)
        postAdapter = PostRecyclerAdapterGrid()
        StaggeredGrid_Explore.adapter = postAdapter
    }

    fun inizializzaBottomMenu(){
        var bottomNavigationMenuView : BottomNavigationView = findViewById(R.id.bottom_explore)
        bottomNavigationMenuView.setSelectedItemId(R.id.explore)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@ExploreActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@ExploreActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@ExploreActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@ExploreActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
            }
            true
        }
    }
}