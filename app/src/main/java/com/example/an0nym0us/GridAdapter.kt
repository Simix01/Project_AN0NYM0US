package com.example.an0nym0us

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

internal class GridAdapter (

    private val postList: List<GridViewModal>,
    private val context: Context
):
        BaseAdapter(){
            private var layoutInflater: LayoutInflater? = null
            private lateinit var post: ImageView
            override fun getCount(): Int {
                return postList.size
            }

            override fun getItem(position: Int): Any? {
                return null
             }

            override fun getItemId(position: Int): Long {
                return 0
             }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var convertView = convertView

                if(layoutInflater == null){
                    layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                }

                if(convertView == null){
                    convertView = layoutInflater!!.inflate(R.layout.grid_item, null)
                }

                post = convertView!!.findViewById(R.id.grid_image)

                post.setImageResource(postList.get(position).postImg)
                return convertView
            }
}