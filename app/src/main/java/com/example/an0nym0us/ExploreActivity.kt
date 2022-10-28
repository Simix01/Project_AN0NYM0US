package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_explore.*
import kotlinx.android.synthetic.main.activity_profile.*

class ExploreActivity : AppCompatActivity() {

    private lateinit var postAdapter: PostRecyclerAdapterGrid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

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
            linear_layout_explore.visibility= View.INVISIBLE
            val mBundle = Bundle()
            mBundle.putParcelable("post",it)
            mFragment.arguments = mBundle
            mFragmentTransaction.add(R.id.fragment_containerExplore, mFragment).commit()
        }
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