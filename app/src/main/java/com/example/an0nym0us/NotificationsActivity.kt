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
    private lateinit var listLikes: ArrayList<String>
    private lateinit var listComments: ArrayList<String>
    private lateinit var listNotifications: ArrayList<String>
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
        listNotifications = arrayListOf<String>()
        getPostData()
    }

    private fun getPostData() {
        dbRef =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti").child("$uId")

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
                            var testo : String = mapApp["user"].toString() + ": ha comentato '"+ mapApp["content"].toString()+"' nel tuo post"
                            listComments.add(0,testo)

                        }

                        if (arrayLikes != null) {
                            for (user in arrayLikes) {
                                if (user != uId)
                                    listLikes.add(0,user + ": ha messo mi piace al tuo post")
                            }
                        }

                        listNotifications.addAll(listComments)
                        listNotifications.addAll(listLikes)
                        Collections.shuffle(listNotifications, Random(System.currentTimeMillis()))
                        notificationAdapter = NotificationRecyclerAdapter(listNotifications)
                        notificationRecyclerView.adapter=notificationAdapter
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

    fun Notification(tipoNotifica: String, testoNotifica: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel: NotificationChannel = NotificationChannel("n","n",NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "prova"
            }
            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this,
            "n")
            .setContentText(tipoNotifica)
            .setSmallIcon(R.drawable.anonym_icon)
            .setAutoCancel(true)
            .setContentText(testoNotifica)

        val managerCompat: NotificationManagerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(999,builder.build())
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