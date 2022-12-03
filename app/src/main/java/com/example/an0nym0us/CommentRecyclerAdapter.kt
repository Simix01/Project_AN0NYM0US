package com.example.an0nym0us

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.comment_item.view.*
import kotlin.math.absoluteValue

class CommentRecyclerAdapter(private val commentList: ArrayList<Commento>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CommentViewHolder -> {
                holder.bind(commentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentUser = itemView.commentUser
        val commentContent = itemView.commentContent
        val layoutCommenti = itemView.linearCommenti

        fun bind(comment: Commento) {
            if(!comment.user.equals(" ") && !comment.content.equals(" ")){
                layoutCommenti.visibility = View.VISIBLE
                commentUser.setText(comment.user)
                commentContent.setText(comment.content)
            }
            else
                layoutCommenti.visibility = View.INVISIBLE
        }
    }
    
}