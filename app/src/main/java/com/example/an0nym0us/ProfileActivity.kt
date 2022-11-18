package com.example.an0nym0us

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.math.absoluteValue

class ProfileActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapterGrid
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefInfo: DatabaseReference
    private lateinit var listFull: ArrayList<Post2>
    private var listNicknames = arrayListOf<String>()
    private var canEdit: String = "false"
    private var canBeFound: String = "false"
    private var trovato: Boolean = false
    private var myNickname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        overridePendingTransition(0, 0)
        inizializzaBottomMenu()
        inizializzaImpostazioni()
        initRecyclerView()
    }

    private fun getPostData() {
        dbRef =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti")

        dbRefInfo =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("InfoUtenti")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (listFull.isEmpty()) {
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

        dbRefInfo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (user in snapshot.children) {

                        var Map = user.getValue() as HashMap<*, *>
                        var nickname = Map["nickname"].toString()

                        if (user.key == uId) {
                            var proPic = Map["proPic"].toString()
                            var approvazioni = Map["approvazioni"].toString()
                            myNickname=nickname
                            canEdit = Map["canEdit"].toString()
                            canBeFound = Map["canBeFound"].toString()
                            var seguiti = Map["seguiti"] as ArrayList<String>
                            var user = Utente(
                                proPic,
                                nickname,
                                approvazioni.toInt(),
                                canEdit.toBoolean(),
                                canBeFound.toBoolean(),
                                seguiti
                            )

                            //setto valore della progressbar
                            progress_bar.setProgress(approvazioni.toInt())
                            var maxValue = progress_bar.max
                            if (progress_bar.progress >= maxValue) {
                                dbRefInfo.child(uId).child("canEdit").setValue(true)
                            } else
                                dbRefInfo.child(uId).child("canEdit").setValue(false)

                            //setto valore del nome del profilo (anonimo/nickname)
                            userCodeProfile.text = myNickname

                            /*verifico se l'user che sta usando l'app è lo stesso della pagina
                        * e in quel caso rendo non visibile il pulsante segui*/
                            if (uId.equals(nickname))
                                followButton.visibility = View.INVISIBLE
                        }

                        listNicknames.add(nickname)

                    }

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

    fun inizializzaImpostazioni() {

        var dialog: Dialog?

        settings.setOnClickListener {
            dialog = Dialog(this@ProfileActivity)
            dialog!!.setContentView(R.layout.impostazioni_profilo)
            dialog!!.window!!.setLayout(1000, 1500)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

            if (this@ProfileActivity != null && !this@ProfileActivity.isFinishing)
                dialog!!.show()

            val buttonNickname = dialog!!.findViewById<Button>(R.id.buttonChangeNickname)
            val buttonPhoto = dialog!!.findViewById<Button>(R.id.buttonChangePhoto)

            if (canEdit.equals("false")) {
                buttonNickname.isClickable = false
                buttonPhoto.isClickable = false
            } else {
                buttonNickname.isClickable = true
                buttonPhoto.isClickable = true
            }

            buttonNickname.setOnClickListener {
                val EditTextName = dialog!!.findViewById<EditText>(R.id.newNicknameText)
                val newName: String = EditTextName.text.toString().trim { it <= ' ' }
                if (newName.equals(""))
                    Toast.makeText(this, "Devi inserire un nuovo nome", Toast.LENGTH_SHORT).show()
                else {
                    if (listNicknames.contains(newName))
                        Toast.makeText(
                            this@ProfileActivity, "Nickname già in uso",
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        dbRefInfo.child("$uId").child("nickname").setValue(newName)
                        Toast.makeText(
                            this@ProfileActivity,
                            "Nickname cambiato con successo",
                            Toast.LENGTH_SHORT
                        ).show()
                        dbRefInfo.child("$uId").child("canBeFound").setValue(true)
                        listNicknames.clear()
                    }
                }
            }
        }
    }
}