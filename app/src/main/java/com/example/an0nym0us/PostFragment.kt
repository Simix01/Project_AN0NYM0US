package com.example.an0nym0us


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
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
                val javaClass = Class.forName(nameActivityFull)
                val intent = Intent(context, javaClass)
                val myBundle = arguments
                val username = myBundle!!.getString("userid").toString()
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
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_post, container, false)

        val bundle = arguments
        val post: Post? = bundle?.getParcelable("post")
        val nameActivity = bundle?.getString("nameActivity").toString()
        nameActivityFull += nameActivity
        var likesList = arrayListOf<String>()
        var dislikesList = arrayListOf<String>()
        var approvazioni: Long? = null
        val uId = bundle?.getString("userid").toString()

        val user = view.findViewById<TextView>(R.id.userCode)
        val userImage=view.findViewById<ImageView>(R.id.userImg)
        val category = view.findViewById<TextView>(R.id.categoriaText)
        val likes = view.findViewById<TextView>(R.id.upvoteCounter)
        val dislikes = view.findViewById<TextView>(R.id.downvoteCounter)
        val image = view.findViewById<ImageView>(R.id.postImageFragment)
        val proPic=view.findViewById<ImageView>(R.id.userImg)
        val likeBtn = view.findViewById<ImageButton>(R.id.likeBtnFrag)
        val dislikeBtn = view.findViewById<ImageButton>(R.id.dislikeBtnFrag)
        val date = view.findViewById<TextView>(R.id.dataPost)
        val commentsBtn = view.findViewById<ImageButton>(R.id.commentsBtn)
        val shareBtn = view.findViewById<ImageButton>(R.id.shareBtn)

        val requestOptionsForProfilePicture = com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.anonym_icon)
            .error(R.drawable.anonym_icon)

        val requestOptionsForPosts = com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.black_screen)
            .error(R.drawable.black_screen)

        if (post != null) {
            if(post.nickname==null)
                user.text = post.user
            else
                user.text = post.nickname

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

            Glide.with(requireContext()).applyDefaultRequestOptions(requestOptionsForPosts)
                .load(post.image)
                .into(image)

            Glide.with(requireContext()).applyDefaultRequestOptions(requestOptionsForProfilePicture)
                .load(post.proPic)
                .into(proPic)

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
                if(likesList.contains(uId))
                    likeBtn.setBackgroundResource(R.drawable.like_button_pressed)
                else
                    likeBtn.setBackgroundResource(R.drawable.like_button_base)
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
                if(dislikesList!!.contains(uId))
                    dislikeBtn.setBackgroundResource(R.drawable.dislike_button_pressed)
                else
                    dislikeBtn.setBackgroundResource(R.drawable.dislike_button_base)
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

            if(likesList.contains(uId))
                likeBtn.setBackgroundResource(R.drawable.like_button_pressed)
            else
                likeBtn.setBackgroundResource(R.drawable.like_button_base)

            if(dislikesList!!.contains(uId))
                dislikeBtn.setBackgroundResource(R.drawable.dislike_button_pressed)
            else
                dislikeBtn.setBackgroundResource(R.drawable.dislike_button_base)
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

            if(likesList.contains(uId))
                likeBtn.setBackgroundResource(R.drawable.like_button_pressed)
            else
                likeBtn.setBackgroundResource(R.drawable.like_button_base)

            if(dislikesList!!.contains(uId))
                dislikeBtn.setBackgroundResource(R.drawable.dislike_button_pressed)
            else
                dislikeBtn.setBackgroundResource(R.drawable.dislike_button_base)
        }

        user.setOnClickListener{
            val intent =
                Intent(activity, ProfileActivity::class.java)
            intent.putExtra("username", post?.user)
            startActivity(intent)
        }

        userImage.setOnClickListener{
            val intent =
                Intent(activity, ProfileActivity::class.java)
            intent.putExtra("username", post?.user)
            startActivity(intent)
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

        shareBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Immagine condivisa da AN0NYM0US," +
                        "scarica l'app anche tu!  ${post!!.image}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
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