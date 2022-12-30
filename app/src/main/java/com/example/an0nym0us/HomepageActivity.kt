package com.example.an0nym0us

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_homepage.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue


class HomepageActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapter
    private lateinit var list: ArrayList<Post2>
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private val dbRefUser =
        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Utenti").child("$uId")
    private val dbRefInfoUtenti =
        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("InfoUtenti")
    private lateinit var myNickname: String
    private var seguiti = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        overridePendingTransition(0, 0)

        dbRefInfoUtenti.child(uId).child("nickname").get().addOnCompleteListener {
            if(it.isSuccessful){
                myNickname = it.result.value as String
            }
        }

        inizializzaBottomMenu()
        initRecyclerView()

        dbRefInfoUtenti.child(uId).child("notifications").get().addOnCompleteListener {
            if(it.isSuccessful){
                if(it.result.value as Boolean == true)
                    avvioService()
            }
        }


    }

    private fun avvioService() {

        dbRefUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var zeroPost = !snapshot.hasChildren()
                if(!zeroPost)
                    ServiceStart()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun ServiceStart() {

            if (!isMyServiceRunning(Notification::class.java)) {
                val intent = Intent(this, Notification::class.java)
                startService(intent)
            }

    }

    private fun isMyServiceRunning(mClass: Class<Notification>): Boolean {
        val manager: ActivityManager = getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {

            if (mClass.name.equals(service.service.className))
                return true
        }
        return false
    }

    private fun initRecyclerView() {
        postRecyclerView = findViewById(R.id.recycler_view)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        postRecyclerView.setHasFixedSize(true)
        list = arrayListOf<Post2>()
        getPostData()
    }

    private fun getPostData() {
        dbRef =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti")

        var dbRefInfo =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("InfoUtenti")

        dbRefInfo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var mapUserInfo=snapshot.value as Map<*, *>
                var myInfo = snapshot.child("$uId").value as HashMap<*,*>
                seguiti = myInfo["seguiti"] as kotlin.collections.ArrayList<String>
                dbRef.addValueEventListener(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (list.isEmpty()) {
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
                                        var comments =
                                            postApp["comments"] as kotlin.collections.ArrayList<Commento>?
                                        var arrayLikes =
                                            postApp["arrayLikes"] as kotlin.collections.ArrayList<String>?
                                        var arrayDislikes =
                                            postApp["arrayDislikes"] as kotlin.collections.ArrayList<String>?

                                        for(userInfo in mapUserInfo){
                                            if(user==userInfo.key) {
                                                var userApp= userInfo.value as kotlin.collections.HashMap<*,*>
                                                var nickname=userApp["nickname"].toString()
                                                var proPic=userApp["proPic"].toString()
                                                var post =
                                                    Post2(
                                                        date,
                                                        image,
                                                        proPic,
                                                        dislikes.toInt(),
                                                        category,
                                                        user,
                                                        nickname,
                                                        likes.toInt(),
                                                        comments,
                                                        arrayLikes,
                                                        arrayDislikes
                                                    )
                                                if(seguiti.contains(user) && dislikes.toInt() < 100 )
                                                    list.add(0, post)
                                            }
                                        }
                                    }
                                }
                            }
                            val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss")

                            list.sortWith (compareBy{ LocalDateTime.parse(it.date, dateTimeFormatter)})
                            list.reverse()
                            postAdapter = PostRecyclerAdapter(list)
                            updateHome()


                            postAdapter.onImageClick = {
                                recycler_view.visibility = View.INVISIBLE
                                swipe_refresh.visibility = View.INVISIBLE

                                val mFragmentManager = supportFragmentManager
                                val mFragmentTransaction = mFragmentManager.beginTransaction()
                                mFragmentTransaction.addToBackStack(null)
                                val mFragment = PostFragment()

                                val mBundle = Bundle()

                                val post = Post(
                                    it.user!!,
                                    it.nickname!!,
                                    it.category!!,
                                    it.date!!,
                                    it.image!!,
                                    it.proPic!!,
                                    it.likes,
                                    it.dislikes
                                )

                                mBundle.putParcelable("post", post)
                                mBundle.putString("nameActivity", "HomepageActivity")
                                mBundle.putString("userid", uId)
                                mFragment.arguments = mBundle
                                mFragmentTransaction.add(R.id.fragment_container, mFragment).commit()
                            }



                            postAdapter.onCommentClick = {
                                recycler_view.visibility = View.INVISIBLE
                                swipe_refresh.visibility = View.INVISIBLE
                                bottom_home.visibility = View.INVISIBLE

                                val mFragmentManager = supportFragmentManager
                                val mFragmentTransaction = mFragmentManager.beginTransaction()
                                mFragmentTransaction.addToBackStack(null)
                                val mFragment2 = comment_fragment()

                                val mBundle = Bundle()

                                mBundle.putString("userPost", it.user)
                                mBundle.putString("actualUser", myNickname)
                                mBundle.putString("datePost", it.date)
                                mBundle.putString("nameActivity", "HomepageActivity")
                                mFragment2.arguments = mBundle
                                mFragmentTransaction.add(R.id.fragment_container, mFragment2).commit()

                            }

                            postAdapter.onShareClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Immagine condivisa da AN0NYM0US," +
                                            "scarica l'app anche tu!  $it")
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                            }

                            postAdapter.onUserClick={
                                val intent =
                                    Intent(this@HomepageActivity, ProfileActivity::class.java)
                                intent.putExtra("username", it)
                                startActivity(intent)
                            }

                            postRecyclerView.adapter = postAdapter

                            if(seguiti[0].equals("ok")){

                                relativeLayout.setBackgroundResource(R.drawable.app_background_variant)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
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
                    intent.putExtra("username", uId)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            true
        }
    }

    fun updateHome(){
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = false
            finish()
            overridePendingTransition(0,0)
            startActivity(Intent(this,HomepageActivity::class.java))
        }
    }

}