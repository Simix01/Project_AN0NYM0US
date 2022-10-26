package com.example.an0nym0us


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

        var uri:Uri=Uri.parse(post?.image )

        var user=view.findViewById<TextView>(R.id.userCode)
        var category=view.findViewById<TextView>(R.id.categoriaText)
        var likes=view.findViewById<TextView>(R.id.upvoteCounter)
        var dislikes=view.findViewById<TextView>(R.id.downvoteCounter)
        var image=view.findViewById<ImageView>(R.id.postImageFragment)


        val requestOptions=com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        if(post!=null) {
            user.text=post.user
            category.text=post.category
            likes.text=post.likes
            dislikes.text=post.dislikes

            Glide.with(requireContext()).applyDefaultRequestOptions(requestOptions).load(post.image).into(image)

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