package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_explore.*
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlin.math.absoluteValue

class ExploreActivity : AppCompatActivity() {
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private  var postAdapter: PostRecyclerAdapterGrid? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var listFull: ArrayList<Post2>
    private lateinit var listAppend: ArrayList<Post2>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        inizializzaBottomMenu()
        initRecyclerView()
    }

    private fun getPostData() {
        dbRef= FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Utenti")

        var chipGroup: ChipGroup = findViewById(R.id.chipGroup)
        chipGroup.checkedChipId

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
                            listFull.add(0,post)
                        }
                    }

                    listAppend.addAll(listFull)
                    postAdapter = PostRecyclerAdapterGrid(listFull)
                    filtraPost()

                    val mFragmentManager = supportFragmentManager
                    val mFragmentTransaction = mFragmentManager.beginTransaction()
                    val mFragment = PostFragment()

                    postAdapter!!.onImageClick={
                        StaggeredGrid_Explore.visibility= View.INVISIBLE
                        val mBundle = Bundle()

                        val post = Post(it.user!!,it.category!!,it.date!!,it.image!!,it.likes,it.dislikes)

                        mBundle.putParcelable("post",post)
                        mFragment.arguments = mBundle
                        mFragmentTransaction.add(R.id.fragment_containerExplore, mFragment).commit()
                    }

                    StaggeredGrid_Explore.adapter = postAdapter
                    postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initRecyclerView() {
        StaggeredGrid_Explore.layoutManager = StaggeredGridLayoutManager(3,LinearLayoutManager.VERTICAL)
        listFull = arrayListOf<Post2>()
        listAppend = arrayListOf<Post2>()
        getPostData()
    }

    fun inizializzaBottomMenu(){
        var bottomNavigationMenuView : BottomNavigationView = findViewById(R.id.bottom_explore)
        bottomNavigationMenuView.setSelectedItemId(R.id.explore)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@ExploreActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@ExploreActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@ExploreActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@ExploreActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
            }
            true
        }
    }

    fun filtraPost(){
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            var listFiltered = arrayListOf<Post2>()
            val idSelected = checkedIds.get(0)
            val chipSelected: com.google.android.material.chip.Chip = findViewById(idSelected)
            val category = chipSelected.text
            for(post in listAppend){
                if(post.category == category)
                    listFiltered.add(post)
            }

            listFull.clear()
            listFull.addAll(listFiltered)
            postAdapter?.notifyDataSetChanged()
        }
    }
}