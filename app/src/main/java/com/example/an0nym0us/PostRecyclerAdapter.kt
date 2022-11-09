package com.example.an0nym0us

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_post_list_item.view.*
import kotlin.math.absoluteValue

class PostRecyclerAdapter(private val postList: ArrayList<Post2>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onImageClick: ((Post2) -> Unit)? = null
    var onCommentClick: ((Post2) -> Unit)? = null
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val post = postList[position]

                var likesList: ArrayList<String>? = null
                var dislikesList: ArrayList<String>? = null

                holder.bind(postList[position])

                holder.postImage.setOnClickListener {
                    onImageClick?.invoke(post)
                }

                holder.commentButton.setOnClickListener{
                    onCommentClick?.invoke(post)
                }


                var dbRefArrayLikes = post.user?.let {
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

                var dbRefArrayDislikes = post.user?.let {
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

                var dbRefLikes = post.user?.let {
                    post.date?.let { it1 ->
                        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Utenti").child(it).child(it1).child("likes")
                    }
                }

                var dbRefDislikes = post.user?.let {
                    post.date?.let { it1 ->
                        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Utenti").child(it).child(it1).child("dislikes")
                    }
                }

                holder.likeButton.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(p0: View?) {

                        if (dislikesList?.contains(uId) == true) {
                            dislikesList!!.remove(uId)
                            post.dislikes = dislikesList!!.size
                            if (dislikesList!!.size == 0)
                                dislikesList!!.add("ok")
                        }
                        if (likesList?.contains(uId) == false) {
                            if (likesList?.get(0) == "ok") {
                                likesList?.removeAt(0)
                                likesList?.add(uId)
                            } else
                                likesList?.add(uId)
                        }else if(likesList?.contains(uId) == true){
                            likesList?.remove(uId)
                            if(likesList!!.size == 0)
                                likesList!!.add("ok")
                        }
                        dbRefArrayLikes!!.setValue(likesList)
                        dbRefArrayDislikes!!.setValue(dislikesList)
                        if (likesList!!.get(0) == "ok")
                            post.likes = likesList!!.size - 1
                        else
                            post.likes = likesList!!.size
                        holder.postLike.setText(post.likes.toString())
                        holder.postDislike.setText(post.dislikes.toString())
                        dbRefLikes?.setValue(post.likes)
                        dbRefDislikes?.setValue(post.dislikes)
                    }


                })
                holder.dislikeButton.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(p0: View?) {


                        if (likesList?.contains(uId) == true) {
                            likesList!!.remove(uId)
                            post.likes = likesList!!.size
                            if (likesList!!.size == 0)
                                likesList!!.add("ok")
                        }
                        if (dislikesList?.contains(uId) == false) {
                            if (dislikesList?.get(0) == "ok") {
                                dislikesList?.removeAt(0)
                                dislikesList?.add(uId)
                            } else
                                dislikesList?.add(uId)
                        }else if(dislikesList?.contains(uId) == true){
                            dislikesList?.remove(uId)
                            if(dislikesList!!.size == 0)
                                dislikesList!!.add("ok")
                        }
                        dbRefArrayDislikes!!.setValue(dislikesList)
                        dbRefArrayLikes!!.setValue(likesList)
                        if (dislikesList!!.get(0) == "ok")
                            post.dislikes = dislikesList!!.size - 1
                        else
                            post.dislikes = dislikesList!!.size
                        holder.postDislike.setText(post.dislikes.toString())
                        holder.postLike.setText(post.likes.toString())
                        dbRefLikes?.setValue(post.likes)
                        dbRefDislikes?.setValue(post.dislikes)

                    }

                })
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage = itemView.post_image
        val postUser = itemView.post_user
        val postCategory = itemView.post_category
        val postDate = itemView.post_data
        var postLike = itemView.post_like
        var postDislike = itemView.post_dislike
        val likeButton = itemView.btnLike
        val dislikeButton = itemView.btnDislike
        val commentButton = itemView.post_comment
        lateinit var dataPost: String

        fun bind(post: Post2) {
            val requestOptions = com.bumptech.glide.request.RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptions).load(post.image)
                .into(postImage)

            postUser.setText(post.user)
            postCategory.setText(post.category)

            var postName = post.date
            if (postName != null) {
                var array = postName.split("_")
                dataPost = array[0] + "/" + array[1] + "/" + array[2]
            }
            postDate.setText(dataPost)

            postLike.setText(post.likes.toString())
            postDislike.setText(post.dislikes.toString())
        }
    }
}