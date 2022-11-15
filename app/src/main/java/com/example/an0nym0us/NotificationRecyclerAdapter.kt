package com.example.an0nym0us

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationRecyclerAdapter (private val notificationList: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationRecyclerAdapter.NotificationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is NotificationRecyclerAdapter.NotificationViewHolder -> {
                holder.bind(notificationList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationView = itemView.notification_TextView

        fun bind(notification: String) {
            notificationView.setText(notification)
        }
    }
}