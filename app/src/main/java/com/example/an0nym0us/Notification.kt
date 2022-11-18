package com.example.an0nym0us

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue

class Notification : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    val GROUP_NOTIFICATION = "com.anonymous.NOTIFICATIONS"
    private val dbRef =
        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Utenti").child("$uId")

    private val dbRefNotificheComments =
        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Notifiche").child("$uId").child("comments")

    private val dbRefNotificheLikes =
        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Notifiche").child("$uId").child("likes")

    var listaCommentsUpdated = arrayListOf<String>()
    var listaCommentsDaVisualizzare = arrayListOf<String>()
    var listaNotifications = arrayListOf<String>()
    var listaLikesUpdated = arrayListOf<String>()
    var listaLikesDaVisualizzare = arrayListOf<String>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var comment = arrayListOf<String>()
        comment.add("")
        var like = arrayListOf<String>()
        like.add("")



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



        dbRefNotificheComments.setValue(comment)
        dbRefNotificheLikes.setValue(like)



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
                            var testo: String =
                                mapApp["user"].toString() + ": ha comentato '" + mapApp["content"].toString() + "' nel tuo post"
                            listaCommentsDaVisualizzare.add(0, testo)
                        }

                        if (arrayLikes != null) {
                            for (i in arrayLikes.indices) {
                                var user = arrayLikes[i]
                                if (user != "ok") {
                                    if (user == uId) {
                                        var testo: String = user + ": ha messo mi piace al tuo post"
                                        listaLikesDaVisualizzare.add(testo)
                                    }
                                }
                            }
                        }
                    }
                }



                if (listaCommentsDaVisualizzare.size > listaCommentsUpdated.size) {


                    var a = listaCommentsDaVisualizzare.size
                    var b = listaCommentsUpdated.size
                    var sizeDiff = a - b

                    for (i in 0..sizeDiff - 1)
                        listaNotifications.add(listaCommentsDaVisualizzare.get(i))

                    listaCommentsUpdated.clear()
                    listaCommentsUpdated.addAll(listaCommentsDaVisualizzare)
                    dbRefNotificheComments.setValue(listaCommentsUpdated)
                }

                if (listaLikesDaVisualizzare.size > listaLikesUpdated.size) {


                    var a = listaLikesDaVisualizzare.size
                    var b = listaLikesUpdated.size
                    var sizeDiff = a - b

                    for (i in 0..sizeDiff - 1)
                        listaNotifications.add(listaLikesDaVisualizzare.get(i))


                    listaLikesUpdated.addAll(listaLikesDaVisualizzare)
                    dbRefNotificheLikes.setValue(listaLikesUpdated)
                }

                if(listaNotifications.isNotEmpty())
                    singleNotification()

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

    private fun singleNotification()
    {

        var inbox: NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()
        for (notification in listaNotifications)
            inbox.addLine(notification)

        var notificationNumber=listaNotifications.size
        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Notifications")
            //set content text to support devices running API level < 24
            .setContentText(notificationNumber.toString()+" new messages")
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
            notify(0, summaryNotification)
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
            channel.lightColor = Color.GREEN
            channel.enableVibration(true)
            val manager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }
    }
}