package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.math.absoluteValue

class ProfileActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapterGrid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        inizializzaBottomMenu()

        val userId: TextView = findViewById(R.id.userCodeProfile)
        userId.text = uId
        initRecyclerView()
        addDataSet()
        imageViewtoFragment()
    }

    private fun imageViewtoFragment() {

        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = PostFragment()

        postAdapter.onImageClick={
            linear_layout_profile.visibility= View.INVISIBLE
            val mBundle = Bundle()
            mBundle.putParcelable("post",it)
            mFragment.arguments = mBundle
            mFragmentTransaction.add(R.id.fragment_containerProfile, mFragment).commit()
        }
    }

    private fun addDataSet() {
        val data = DataSource.createDataSet()
        postAdapter.submitList(data)
    }

    private fun initRecyclerView() {
        grid_post.layoutManager = GridLayoutManager(this, 3)
        postAdapter = PostRecyclerAdapterGrid()
        grid_post.adapter = postAdapter
    }

    fun inizializzaBottomMenu() {
        var bottomNavigationMenuView: BottomNavigationView = findViewById(R.id.bottom_profile)
        bottomNavigationMenuView.setSelectedItemId(R.id.profile)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@ProfileActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.explore -> {
                    val intent =
                        Intent(this@ProfileActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@ProfileActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@ProfileActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            true
        }
    }

}