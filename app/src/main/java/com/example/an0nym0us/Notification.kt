package com.example.an0nym0us

import android.app.Notification
import android.app.Notification.InboxStyle
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private val listNotifications= arrayListOf<String>()
    private val CHANNEL_ID="ForegroundService Kotlin"
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    val GROUP_NOTIFICATION="com.anonymous.NOTIFICATIONS"
    private val dbRef = FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
    .getReference("Utenti").child("$uId")


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listNotifications.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        var postApp = userSnapshot.getValue() as HashMap<*, *>
                        var arrayLikes =
                            postApp["arrayLikes"] as kotlin.collections.ArrayList<String>?
                        var comments=postApp["comments"] as ArrayList<Commento>


                        for(i in comments.indices) {
                            var mapApp = comments.get(i) as HashMap<*, *>
                            var testo : String = mapApp["user"].toString() + ": ha comentato '"+ mapApp["content"].toString()+"' nel tuo post"
                            listNotifications.add(testo)
                        }

                        if (arrayLikes != null) {
                            for (user in arrayLikes) {
                                if (user != uId) {
                                    var testo: String = user + ": ha messo mi piace al tuo post"
                                    listNotifications.add(testo)
                                }
                            }
                        }
                        singleNotification()
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
        var inbox: NotificationCompat.InboxStyle =NotificationCompat.InboxStyle()
        for(notification in listNotifications)
            inbox.addLine(notification)
        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Notifications")
            //set content text to support devices running API level < 24
            .setContentText("Two new messages")
            .setSmallIcon(R.drawable.anonym_icon)
            //build summary info into InboxStyle template
            .setStyle(inbox)
            //specify which group this notification belongs to
            .setGroup(GROUP_NOTIFICATION)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).apply {
            notify(0,summaryNotification)
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                CHANNEL_ID, "AN0NYM0US",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "prova"
            }
            channel.enableLights(true)
            channel.lightColor= Color.GREEN
            channel.enableVibration(true)
            val manager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}