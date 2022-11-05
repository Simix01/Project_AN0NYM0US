package com.example.an0nym0us

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_post_list_item.view.*

class PostRecyclerAdapter(private val postList : ArrayList<Post2>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onImageClick: ((Post2) -> Unit)? = null


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


                var btnClickedOnce:Boolean=false
                var likeBtnClicked:Boolean=false
                var dislikeBtnClicked:Boolean=false
                holder.bind(postList[position])
                holder.postImage.setOnClickListener {
                    onImageClick?.invoke(post)
                }
                holder.likeButton.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(p0: View?) {
                        var dbRef = post.user?.let {
                            post.date?.let { it1 ->
                                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("Utenti").child(it).child(it1).child("likes")
                            }
                        }
                        if(!likeBtnClicked&&!btnClickedOnce){
                            post.likes++
                            holder.postLike.setText(post.likes.toString())
                            likeBtnClicked=true
                            btnClickedOnce=true
                            dbRef?.setValue(post.likes++)
                        }
                    }
                })
                holder.dislikeButton.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(p0: View?) {
                        var dbRef = post.user?.let {
                            post.date?.let { it1 ->
                                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("Utenti").child(it).child(it1).child("dislikes")
                            }
                        }
                        if(!dislikeBtnClicked&&!btnClickedOnce){
                            post.dislikes++
                            holder.postDislike.setText(post.dislikes.toString())
                            dislikeBtnClicked=true
                            btnClickedOnce=true
                            dbRef?.setValue(post.dislikes++)
                        }
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