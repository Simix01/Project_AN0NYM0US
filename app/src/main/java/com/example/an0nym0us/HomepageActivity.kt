package com.example.an0nym0us

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.layout_post_list_item.*
import kotlin.math.absoluteValue


class HomepageActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        inizializzaBottomMenu()
        initRecyclerView()
        addDataSet()
        imageViewtoFragment()
    }

    private fun imageViewtoFragment() {

        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = PostFragment()

        postAdapter.onImageClick={
            recycler_view.visibility= View.INVISIBLE
            val mBundle = Bundle()
            mBundle.putParcelable("post",it)
            mFragment.arguments = mBundle
            mFragmentTransaction.add(R.id.fragment_container, mFragment).commit()
        }
    }

    private fun addDataSet() {
        val data = DataSource.createDataSet()
        postAdapter.submitList(data)
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        postAdapter = PostRecyclerAdapter()
        recycler_view.adapter = postAdapter
    }

    private fun inizializzaBottomMenu() {
        var bottomNavigationMenuView: BottomNavigationView = findViewById(R.id.bottom_home)
        bottomNavigationMenuView.setSelectedItemId(R.id.homepage)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.explore -> {
                    val intent =
                        Intent(this@HomepageActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@HomepageActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@HomepageActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@HomepageActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            true
        }
    }
}