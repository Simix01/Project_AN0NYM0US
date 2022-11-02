package com.example.an0nym0us

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlin.math.absoluteValue


class HomepageActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapter
    private lateinit var list: ArrayList<Post2>
    private lateinit var postRecyclerView:RecyclerView
    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        inizializzaBottomMenu()
        initRecyclerView()

    }

    private fun initRecyclerView() {
        postRecyclerView=findViewById(R.id.recycler_view)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        postRecyclerView.setHasFixedSize(true)
        list= arrayListOf<Post2>()
        getPostData()
    }

    private fun getPostData() {
        dbRef=FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Utenti")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(userSnapshot in snapshot.children) {
                        for(postSnapshot in userSnapshot.children) {
                            var postApp= postSnapshot.getValue() as HashMap<*,*>
                            var user = postApp["user"].toString()
                            var category = postApp["category"].toString()
                            var date = postApp["date"].toString()
                            var image = postApp["image"].toString()
                            var likes = postApp["likes"].toString()
                            var dislikes = postApp["dislikes"].toString()
                            var post =  Post2(date,image, dislikes.toInt(),category,user,likes.toInt())
                            list.add(0,post)
                        }
                    }
                    postAdapter = PostRecyclerAdapter(list)

                    val mFragmentManager = supportFragmentManager
                    val mFragmentTransaction = mFragmentManager.beginTransaction()
                    val mFragment = PostFragment()

                    postAdapter.onImageClick={
                        recycler_view.visibility= View.INVISIBLE
                        val mBundle = Bundle()

                        val post = Post(it.user!!,it.category!!,it.date!!,it.image!!,it.likes,it.dislikes)

                        mBundle.putParcelable("post",post)
                        mFragment.arguments = mBundle
                        mFragmentTransaction.add(R.id.fragment_container, mFragment).commit()
                    }

                    postRecyclerView.adapter = postAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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