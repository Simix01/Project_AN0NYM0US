package com.example.an0nym0us

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.bottom_home
import kotlin.math.absoluteValue

class ProfileActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapterGrid
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefInfo: DatabaseReference
    private lateinit var listFull: ArrayList<Post2>
    //private lateinit var approvazioni: Int


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        overridePendingTransition(0, 0)
        inizializzaBottomMenu()

        val userId: TextView = findViewById(R.id.userCodeProfile)
        userId.text = uId

        initRecyclerView()
    }

    private fun getPostData() {
        dbRef= FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Utenti")

        dbRefInfo= FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("InfoUtenti").child("$uId")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(listFull.isEmpty()) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            for (postSnapshot in userSnapshot.children) {

                                var postApp = postSnapshot.getValue() as HashMap<*, *>
                                var user = postApp["user"].toString()
                                var category = postApp["category"].toString()
                                var date = postApp["date"].toString()
                                var image = postApp["image"].toString()
                                var likes = postApp["likes"].toString()
                                var dislikes = postApp["dislikes"].toString()
                                var comments = postApp["comments"] as ArrayList<Commento>?
                                var arrayLikes = postApp["arrayLikes"] as ArrayList<String>?
                                var arrayDislikes = postApp["arrayDislikes"] as ArrayList<String>?
                                var post = Post2(
                                    date,
                                    image,
                                    dislikes.toInt(),
                                    category,
                                    user,
                                    likes.toInt(),
                                    comments,
                                    arrayLikes,
                                    arrayDislikes
                                )

                                if (user.equals(uId)) {
                                    listFull.add(0, post)
                                }
                            }
                        }


                        postAdapter = PostRecyclerAdapterGrid(listFull)

                        val mFragmentManager = supportFragmentManager
                        val mFragmentTransaction = mFragmentManager.beginTransaction()
                        val mFragment = PostFragment()

                        postAdapter!!.onImageClick = {
                            grid_post.visibility = View.INVISIBLE
                            linear_layout_profile.visibility = View.INVISIBLE

                            val mBundle = Bundle()

                            val post = Post(
                                it.user!!,
                                it.category!!,
                                it.date!!,
                                it.image!!,
                                it.likes,
                                it.dislikes
                            )

                            mBundle.putParcelable("post", post)
                            mBundle.putString("nameActivity", "ProfileActivity")
                            mBundle.putString("userid", uId)
                            mFragment.arguments = mBundle
                            mFragmentTransaction.add(R.id.fragment_containerProfile, mFragment)
                                .commit()
                        }

                        grid_post.adapter = postAdapter
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbRefInfo.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                        var Map= snapshot.getValue() as HashMap<*,*>
                        var proPic = Map["proPic"].toString()
                        var nickname = Map["nickname"].toString()
                        var approvazioni = Map["approvazioni"].toString()
                        var canEdit = Map["canEdit"].toString()
                        var canBeFound = Map["canBeFound"].toString()
                        var user = Utente(proPic,nickname,approvazioni.toInt(),canEdit.toBoolean(),canBeFound.toBoolean())

                        progress_bar.setProgress(approvazioni.toInt())

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun initRecyclerView() {
        grid_post.layoutManager = GridLayoutManager(this, 3)
        listFull = arrayListOf<Post2>()
        getPostData()
    }

    fun inizializzaBottomMenu() {
        var bottomNavigationMenuView: BottomNavigationView = findViewById(R.id.bottom_home)
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