package com.example.an0nym0us

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue

class Notification: Service() {
    private var tipoNotifica: String? = null
    private var testoNotifica: String? = null
    private val CHANNEL_ID="ForegroundService Kotlin"
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private val dbRef = FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
    .getReference("Utenti").child("$uId")


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

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
                            tipoNotifica = "E' stato aggiunto un commento ad un tuo post"
                            testoNotifica= testo
                            singleNotification()

                        }

                        if (arrayLikes != null) {
                            for (user in arrayLikes) {
                                if (user != uId) {
                                    var testo: String = user + ": ha messo mi piace al tuo post"
                                    tipoNotifica = "E' stato messo 'mi piace' ad un tuo post"
                                    testoNotifica = testo
                                    singleNotification()
                                }
                            }
                        }
                    }
                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        createNotificationChannel()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun singleNotification(){
        val notificationIntent = Intent(this,LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0)

        val notification : Notification = NotificationCompat.Builder(this,
            CHANNEL_ID)
            .setContentText(tipoNotifica)
            .setSmallIcon(R.drawable.anonym_icon)
            .setAutoCancel(true)
            .setContentText(testoNotifica)
            .build()
        startForeground(1,notification)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                CHANNEL_ID, "AN0NYM0US",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "prova"
            }
            val manager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}