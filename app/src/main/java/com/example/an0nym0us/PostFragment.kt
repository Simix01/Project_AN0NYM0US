package com.example.an0nym0us


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.fragment_post.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PostFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    var nameActivityFull: String = "com.example.an0nym0us."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var javaClass = Class.forName(nameActivityFull)
                val intent = Intent(context, javaClass)
                val myBundle = arguments
                var username = myBundle!!.getString("userid").toString()
                intent.putExtra("username", username)
                startActivity(intent)
            }

        })
        arguments?.let {

            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_post, container, false)

        val bundle = arguments
        val post: Post? = bundle?.getParcelable("post")
        var nameActivity = bundle?.getString("nameActivity").toString()
        nameActivityFull += nameActivity
        var likesList = arrayListOf<String>()
        var dislikesList: ArrayList<String>? = null
        var approvazioni: Long? = null
        var uId = bundle?.getString("userid").toString()

        var user = view.findViewById<TextView>(R.id.userCode)
        var category = view.findViewById<TextView>(R.id.categoriaText)
        var likes = view.findViewById<TextView>(R.id.upvoteCounter)
        var dislikes = view.findViewById<TextView>(R.id.downvoteCounter)
        var image = view.findViewById<ImageView>(R.id.postImageFragment)
        var likeBtn = view.findViewById<ImageButton>(R.id.likeBtnFrag)
        var dislikeBtn = view.findViewById<ImageButton>(R.id.dislikeBtnFrag)
        var date = view.findViewById<TextView>(R.id.dataPost)
        var commentsBtn = view.findViewById<ImageButton>(R.id.commentsBtn)

        val requestOptions = com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        if (post != null) {
            user.text = post.user
            category.text = post.category
            likes.text = post.likes.toString()
            dislikes.text = post.dislikes.toString()

            var postName = post.date
            var dataPost: String? = null
            if (postName != null) {
                var array = postName.split("_")
                dataPost = array[0] + "/" + array[1] + "/" + array[2]
            }
            date.text = dataPost

            Glide.with(requireContext()).applyDefaultRequestOptions(requestOptions).load(post.image)
                .into(image)

        }

        var dbRefArrayLikes = post?.user?.let {
            post.date?.let { it1 ->
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(it).child(it1).child("arrayLikes")
            }
        }
        dbRefArrayLikes?.get()?.addOnCompleteListener {
            if (it.isSuccessful) {
                likesList = it.result.value as ArrayList<String>

            }
        }

        var dbRefArrayDislikes = post?.user?.let {
            post.date?.let { it1 ->
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(it).child(it1).child("arrayDislikes")
            }
        }
        dbRefArrayDislikes?.get()?.addOnCompleteListener {
            if (it.isSuccessful) {
                dislikesList = it.result.value as ArrayList<String>
            }
        }

        var dbRefLikes = post?.user?.let {
            post.date?.let { it1 ->
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(it).child(it1).child("likes")
            }
        }

        var dbRefDislikes = post?.user?.let {
            post.date?.let { it1 ->
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(it).child(it1).child("dislikes")
            }
        }

        var dbRefApprovazioni = post?.user?.let { it ->
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("InfoUtenti").child(it).child("approvazioni")
        }
        dbRefApprovazioni?.get()?.addOnCompleteListener {
            if (it.isSuccessful) {
                approvazioni = it.result.value as Long?
            }
        }

        likeBtn.setOnClickListener {
            if (dislikesList?.contains(uId) == true) {
                dislikesList!!.remove(uId)
                post?.dislikes = dislikesList!!.size
                if (dislikesList!!.size == 0)
                    dislikesList!!.add("ok")
            }

            if (likesList?.contains(uId) == false) {
                if (likesList.get(0) == "ok") {
                    likesList!!.removeAt(0)
                    likesList?.add(uId)
                    approvazioni = approvazioni?.plus(1)
                    dbRefApprovazioni!!.setValue(approvazioni)
                } else {
                    likesList?.add(uId)
                    approvazioni = approvazioni?.plus(1)
                    dbRefApprovazioni!!.setValue(approvazioni)
                }
            } else if (likesList?.contains(uId) == true) {

                likesList?.remove(uId)
                if (approvazioni!!.minus(1) < 0)
                    dbRefApprovazioni!!.setValue(0)
                else {
                    approvazioni = approvazioni?.minus(1)
                    dbRefApprovazioni!!.setValue(approvazioni)
                }
                if (likesList!!.size == 0)
                    likesList!!.add("ok")
            }

            dbRefArrayLikes!!.setValue(likesList)
            dbRefArrayDislikes!!.setValue(dislikesList)



            if (likesList?.get(0) == "ok")
                post?.likes = likesList!!.size - 1
            else
                post?.likes = likesList!!.size
            likes.text = (post!!.likes).toString()
            dislikes.text = (post!!.dislikes).toString()
            dbRefLikes?.setValue(post.likes)
            dbRefDislikes?.setValue(post.dislikes)
        }


        dislikeBtn.setOnClickListener {
            if (likesList?.contains(uId) == true) {

                likesList!!.remove(uId)
                post?.likes = likesList!!.size
                if (likesList!!.size == 0)
                    likesList!!.add("ok")
            }

            if (dislikesList?.contains(uId) == false) {
                if (dislikesList?.get(0) == "ok") {
                    dislikesList?.removeAt(0)
                    dislikesList?.add(uId)
                    if (approvazioni?.minus(1)!! < 0)
                        dbRefApprovazioni!!.setValue(0)
                    else {
                        approvazioni = approvazioni?.minus(1)
                        dbRefApprovazioni!!.setValue(approvazioni)
                    }
                } else {
                    dislikesList?.add(uId)
                    if (approvazioni?.minus(1)!! < 0)
                        dbRefApprovazioni!!.setValue(0)
                    else {
                        approvazioni = approvazioni?.minus(1)
                        dbRefApprovazioni!!.setValue(approvazioni)
                    }
                }

            } else if (dislikesList?.contains(uId) == true) {
                dislikesList?.remove(uId)
                approvazioni = approvazioni?.plus(1)
                dbRefApprovazioni!!.setValue(approvazioni)
                if (dislikesList!!.size == 0)
                    dislikesList!!.add("ok")
            }

            dbRefArrayDislikes!!.setValue(dislikesList)
            dbRefArrayLikes!!.setValue(likesList)
            if (dislikesList!!.get(0) == "ok")
                post!!.dislikes = dislikesList!!.size - 1
            else
                post!!.dislikes = dislikesList!!.size
            dislikes.text = (post.dislikes).toString()
            likes.text = (post.likes).toString()
            dbRefLikes?.setValue(post.likes)
            dbRefDislikes?.setValue(post.dislikes)
        }


        commentsBtn.setOnClickListener {

            var bottom_home = activity?.findViewById<BottomNavigationView>(R.id.bottom_home)
            if (bottom_home != null) {
                bottom_home.visibility = View.INVISIBLE
            }

            val commentFragment = comment_fragment()

            val mBundle = Bundle()

            mBundle.putString("userPost", post?.user)
            mBundle.putString("actualUser", uId)
            mBundle.putString("datePost", post?.date)
            mBundle.putString("nameActivity", nameActivity)
            commentFragment.arguments = mBundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()


            when (nameActivity) {

                "HomepageActivity" -> transaction?.replace(R.id.fragment_container, commentFragment)
                    ?.commit()
                "ExploreActivity" -> transaction?.replace(
                    R.id.fragment_containerExplore,
                    commentFragment
                )?.commit()
                "ProfileActivity" -> transaction?.replace(
                    R.id.fragment_containerProfile,
                    commentFragment
                )?.commit()

            }

        }


        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}