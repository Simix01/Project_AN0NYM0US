package com.example.an0nym0us

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostRecyclerAdapter (var context: Context, var dataSource:ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items:List<Post> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_post_list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(postList: List<Post>){
        items=postList
    }

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postImage=itemView.post_image
        val postUser=itemView.post_user
        val postCategory=itemView.post_category
        val postDate=itemView.post_date
        val postLike=itemView.post_like
        val postDislike=itemView.post_dislike
        val postComment=itemView.post_comment
        val postShare=itemView.post_share

        fun bind(post: Post){
            val requestOptions=com.bumptech.glide.request.RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptions).load(post.image).into(postImage)
        }

    }
}