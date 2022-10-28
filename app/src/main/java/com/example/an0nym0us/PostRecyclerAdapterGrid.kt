package com.example.an0nym0us

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.grid_item.view.*


class PostRecyclerAdapterGrid: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onImageClick : ((Post) -> Unit)? = null
    private var dataSource: List<Post> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostViewHolder -> {
                val post = dataSource[position]
                holder.bind(dataSource[position])
                holder.postImage.setOnClickListener{
                    onImageClick?.invoke(post)
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    fun submitList(postList:List<Post>){
        dataSource=postList
    }

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postImage=itemView.grid_image
        fun bind(post: Post){
            val requestOptions=com.bumptech.glide.request.RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptions).load(post.image).into(postImage)
        }
    }
}