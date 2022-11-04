package com.example.an0nym0us


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_post, container, false)

        val bundle = arguments
        val post: Post? = bundle?.getParcelable("post")

        var likeBtnClicked:Boolean=false
        var btnClickedOnce:Boolean=false
        var dislikeBtnClicked:Boolean=false

        var user=view.findViewById<TextView>(R.id.userCode)
        var category=view.findViewById<TextView>(R.id.categoriaText)
        var likes=view.findViewById<TextView>(R.id.upvoteCounter)
        var dislikes=view.findViewById<TextView>(R.id.downvoteCounter)
        var image=view.findViewById<ImageView>(R.id.postImageFragment)
        var likeBtn=view.findViewById<ImageButton>(R.id.likeBtnFrag)
        var dislikeBtn=view.findViewById<ImageButton>(R.id.dislikeBtnFrag)

        val requestOptions=com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        if(post!=null) {
            user.text=post.user
            category.text=post.category
            likes.text= post.likes.toString()
            dislikes.text= post.dislikes.toString()

            Glide.with(requireContext()).applyDefaultRequestOptions(requestOptions).load(post.image).into(image)

        }

        likeBtn.setOnClickListener{
            if (post != null) {
                var dbRef = FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(post.user).child(post.date).child("likes")
                if(!likeBtnClicked&&!btnClickedOnce) {
                    likes.text = (post.likes + 1).toString()
                    dbRef.setValue(post.likes + 1)
                    likeBtnClicked=true
                    btnClickedOnce=true
                }
            }
        }

        dislikeBtn.setOnClickListener{
            if (post != null) {
            var dbRef = FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti").child(post.user).child(post.date).child("dislikes")
                if(!dislikeBtnClicked&&!btnClickedOnce) {
                    dislikes.text = (post.dislikes + 1).toString()
                    dbRef.setValue(post.dislikes + 1)
                    dislikeBtnClicked=true
                    btnClickedOnce=true
                }
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