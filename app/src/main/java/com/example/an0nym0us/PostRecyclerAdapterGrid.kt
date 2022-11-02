package com.example.an0nym0us

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.grid_item.view.*


class PostRecyclerAdapterGrid(private val postList: ArrayList<Post2>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onImageClick : ((Post2) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostViewHolder -> {
                val post = postList[position]
                holder.bind(postList[position])
                holder.postImage.setOnClickListener{
                    onImageClick?.invoke(post)
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postImage=itemView.grid_image
        fun bind(post: Post2){
            val requestOptions=com.bumptech.glide.request.RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptions).load(post.image).into(postImage)
        }
    }
}