package com.example.an0nym0us

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue

class NotificationsActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var notificationAdapter: NotificationRecyclerAdapter
    private lateinit var listaCommentsUpdated: ArrayList<String>
    private lateinit var listaCommentsDaVisualizzare: ArrayList<String>
    private lateinit var listaNotifications: ArrayList<String>
    private lateinit var listaLikesUpdated: ArrayList<String>
    private lateinit var listaLikesDaVisualizzare: ArrayList<String>
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

        listaCommentsUpdated = arrayListOf<String>()
        listaCommentsDaVisualizzare = arrayListOf<String>()
        listaNotifications = arrayListOf<String>()
        listaLikesUpdated = arrayListOf<String>()
        listaLikesDaVisualizzare = arrayListOf<String>()

        getPostData()
    }

    private fun getPostData() {
        dbRef =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti").child("$uId")


        val dbRefNotificheComments =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Notifiche").child("$uId").child("comments")

        val dbRefNotificheLikes =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Notifiche").child("$uId").child("likes")



        dbRefNotificheComments.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listaCommentsUpdated = snapshot.value as ArrayList<String>
                    if (listaCommentsUpdated.get(0) == "")
                        listaCommentsUpdated.removeAt(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dbRefNotificheLikes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listaLikesUpdated = snapshot.value as ArrayList<String>
                    if (listaLikesUpdated.get(0) == "")
                        listaLikesUpdated.removeAt(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaLikesDaVisualizzare.clear()
                listaCommentsDaVisualizzare.clear()
                listaNotifications.clear()
                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {
                        var postApp = userSnapshot.getValue() as HashMap<*, *>
                        var arrayLikes =
                            postApp["arrayLikes"] as kotlin.collections.ArrayList<String>?
                        var comments = postApp["comments"] as ArrayList<Commento>


                        for (i in comments.indices) {
                            var mapApp = comments[i] as HashMap<*, *>
                            if (mapApp["user"].toString() != uId && mapApp["user"].toString() != " ") {
                                var testo: String =
                                    mapApp["user"].toString() + ": ha comentato '" + mapApp["content"].toString() + "' nel tuo post"
                                listaNotifications.add(0,testo)
                            }
                        }

                        if (arrayLikes != null) {
                            for (i in arrayLikes.indices) {
                                var user = arrayLikes[i]
                                if (user != "ok") {
                                    if (user != uId) {
                                        var testo: String = user + ": ha messo mi piace al tuo post"
                                        listaNotifications.add(0,testo)
                                    }
                                }
                            }
                        }
                    }
                }
                if (listaNotifications.isNotEmpty()) {
                    notificationAdapter = NotificationRecyclerAdapter(listaNotifications)
                    notificationRecyclerView.adapter = notificationAdapter
                }
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