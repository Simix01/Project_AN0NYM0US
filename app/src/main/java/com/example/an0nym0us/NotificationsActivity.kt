package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.absoluteValue

class NotificationsActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var notificationAdapter: NotificationRecyclerAdapter
    private lateinit var listLikes: ArrayList<String>
    private lateinit var listComments: ArrayList<String>
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        inizializzaBottomMenu()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        notificationRecyclerView = findViewById(R.id.notification_RecycleView)
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationRecyclerView.setHasFixedSize(true)
        listLikes = arrayListOf<String>()
        listComments = arrayListOf<String>()
        getPostData()
    }

    private fun getPostData() {
        dbRef =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti").child("anonym1191829010")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        var postApp = userSnapshot.getValue() as HashMap<*, *>
                        var arrayLikes =
                            postApp["arrayLikes"] as kotlin.collections.ArrayList<String>?
                        var comments=postApp["comments"] as ArrayList<Commento>


                        for(i in comments.indices) {
                            var mapApp = comments.get(i) as HashMap<*, *>
                            listComments.add(mapApp["user"].toString() + ": ha comentato '"+ mapApp["content"].toString()+"' nel tuo post")
                        }

                        if (arrayLikes != null) {
                            for (user in arrayLikes) {
                                if (user != uId)
                                    listLikes.add(user + " ha messo mi piace al tuo post")
                            }
                        }
                    }

                }
                var listNotification = arrayListOf<String>()
                listNotification.addAll(listLikes)
                listNotification.addAll(listComments)
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun inizializzaBottomMenu() {
        var bottomNavigationMenuView: BottomNavigationView = findViewById(R.id.bottom_notifications)
        bottomNavigationMenuView.setSelectedItemId(R.id.notifications)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@NotificationsActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.explore -> {
                    val intent =
                        Intent(this@NotificationsActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@NotificationsActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@NotificationsActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            true
        }
    }
}