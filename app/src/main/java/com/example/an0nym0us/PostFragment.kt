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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_post.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PostFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    var nameActivity:String = "com.example.an0nym0us."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                var javaClass=Class.forName(nameActivity)
                val intent = Intent(context, javaClass)
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
        nameActivity+= bundle?.getString("nameActivity").toString()
        var likeBtnClicked:Boolean=false
        var btnClickedOnce:Boolean=false
        var dislikeBtnClicked:Boolean=false
        var likesList: ArrayList<String>? = null
        var dislikesList: ArrayList<String>? = null
        var uId = bundle?.getString("userid").toString()

        var user=view.findViewById<TextView>(R.id.userCode)
        var category=view.findViewById<TextView>(R.id.categoriaText)
        var likes=view.findViewById<TextView>(R.id.upvoteCounter)
        var dislikes=view.findViewById<TextView>(R.id.downvoteCounter)
        var image=view.findViewById<ImageView>(R.id.postImageFragment)
        var likeBtn=view.findViewById<ImageButton>(R.id.likeBtnFrag)
        var dislikeBtn=view.findViewById<ImageButton>(R.id.dislikeBtnFrag)
        var date = view.findViewById<TextView>(R.id.dataPost)

        val requestOptions=com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        if(post!=null) {
            user.text=post.user
            category.text=post.category
            likes.text= post.likes.toString()
            dislikes.text= post.dislikes.toString()

            var postName = post.date
            var dataPost: String? = null
            if (postName != null) {
                var array = postName.split("_")
                dataPost = array[0] + "/" + array[1] + "/" + array[2]
            }
            date.text = dataPost

            Glide.with(requireContext()).applyDefaultRequestOptions(requestOptions).load(post.image).into(image)

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

        likeBtn.setOnClickListener{
            if (dislikesList?.contains(uId) == true) {
                dislikesList!!.remove(uId)
                post!!.dislikes = dislikesList!!.size
                if (dislikesList!!.size == 0)
                    dislikesList!!.add("ok")
            }
            if (likesList?.contains(uId) == false) {
                if (likesList?.get(0) == "ok") {
                    likesList?.removeAt(0)
                    likesList?.add(uId)
                } else
                    likesList?.add(uId)
            }
            dbRefArrayLikes!!.setValue(likesList)
            dbRefArrayDislikes!!.setValue(dislikesList)
            if (likesList!!.get(0) == "ok")
                post!!.likes = likesList!!.size - 1
            else
                post!!.likes = likesList!!.size
            likes.text = (post!!.likes).toString()
            dislikes.text = (post!!.dislikes).toString()
            likeBtnClicked = true
            btnClickedOnce = true
            dbRefLikes?.setValue(post.likes)
            dbRefDislikes?.setValue(post.dislikes)
        }


        dislikeBtn.setOnClickListener{
            if (likesList?.contains(uId) == true) {
                likesList!!.remove(uId)
                post!!.likes = likesList!!.size
                if (likesList!!.size == 0)
                    likesList!!.add("ok")
            }
            if (dislikesList?.contains(uId) == false) {
                if (dislikesList?.get(0) == "ok") {
                    dislikesList?.removeAt(0)
                    dislikesList?.add(uId)
                } else
                    dislikesList?.add(uId)
            }
            dbRefArrayDislikes!!.setValue(dislikesList)
            dbRefArrayLikes!!.setValue(likesList)
            if (dislikesList!!.get(0) == "ok")
                post!!.dislikes = dislikesList!!.size - 1
            else
                post!!.dislikes = dislikesList!!.size
            dislikes.text = (post.dislikes).toString()
            likes.text = (post.likes).toString()
            dislikeBtnClicked = true
            btnClickedOnce = true
            dbRefLikes?.setValue(post.likes)
            dbRefDislikes?.setValue(post.dislikes)
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