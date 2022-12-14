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
    var onShareClick: ((String) -> Unit)? = null
    var onUserClick: ((String) -> Unit)? = null
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    var myNickname: String? = null
    var likesList: ArrayList<String>? = null
    var dislikesList: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var dbRefNickname = FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("InfoUtenti").child(uId).child("nickname")
        dbRefNickname.get().addOnCompleteListener {
            if(it.isSuccessful){
                myNickname = it.result.value as String
            }
        }

        when (holder) {
            is PostViewHolder -> {
                val post = postList[position]

                var approvazioni: Long? = null

                holder.bind(postList[position])

                holder.postImage.setOnClickListener {
                    onImageClick?.invoke(post)
                }

                holder.commentButton.setOnClickListener {
                    onCommentClick?.invoke(post)
                }

                holder.postUser.setOnClickListener{
                    post.user?.let { it1 -> onUserClick?.invoke(it1) }
                }

                holder.proPic.setOnClickListener{
                    post.user?.let { it1 -> onUserClick?.invoke(it1) }
                }

                holder.shareButton.setOnClickListener {
                    onShareClick?.invoke(post.image!!)
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

                var dbRefApprovazioni = post.user?.let { it ->
                    FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("InfoUtenti").child(it).child("approvazioni")
                }
                dbRefApprovazioni?.get()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        approvazioni = it.result.value as Long?
                    }
                }
                val dbRefNotificheLikes =
                    post.user?.let {
                        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Notifiche").child(it).child("likes")
                    }
                holder.likeButton.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(p0: View?) {

                        //se premo like e ho messo un dislike allora lo toglie
                        if (dislikesList?.contains(myNickname) == true) {
                            dislikesList!!.remove(myNickname)
                            post.dislikes = dislikesList!!.size
                            if (dislikesList!!.size == 0)
                                dislikesList!!.add("ok")
                        }

                        if (likesList?.contains(myNickname) == false) {
                            if (likesList!!.get(0) == "ok") {
                                likesList!!.removeAt(0)
                                likesList?.add(myNickname!!)
                                approvazioni = approvazioni?.plus(1)
                                dbRefApprovazioni!!.setValue(approvazioni)
                            } else {
                                likesList?.add(myNickname!!)
                                approvazioni = approvazioni?.plus(1)
                                dbRefApprovazioni!!.setValue(approvazioni)
                            }
                        } else if (likesList?.contains(myNickname) == true) {
                            likesList?.remove(myNickname)
                            if (approvazioni!!.minus(1) < 0)
                                dbRefApprovazioni!!.setValue(0)
                            else {
                                approvazioni = approvazioni?.minus(1)
                                dbRefApprovazioni!!.setValue(approvazioni)
                            }
                            if (likesList!!.size == 0)
                                likesList!!.add("ok")
                            var app = ArrayList<String>()
                            app.add("")
                            dbRefNotificheLikes!!.setValue(app)
                        }

                        dbRefArrayLikes!!.setValue(likesList)
                        dbRefArrayDislikes!!.setValue(dislikesList)

                        if (likesList?.get(0) == "ok")
                            post.likes = likesList!!.size - 1
                        else
                            post.likes = likesList!!.size

                        holder.postLike.setText(post.likes.toString())
                        holder.postDislike.setText(post.dislikes.toString())
                        dbRefLikes?.setValue(post.likes)
                        dbRefDislikes?.setValue(post.dislikes)

                        if(likesList!!.contains(myNickname))
                            holder.likeButton.setBackgroundResource(R.drawable.like_button_pressed)
                        else
                            holder.likeButton.setBackgroundResource(R.drawable.like_button_base)

                        if(dislikesList!!.contains(myNickname))
                            holder.dislikeButton.setBackgroundResource(R.drawable.dislike_button_pressed)
                        else
                            holder.dislikeButton.setBackgroundResource(R.drawable.dislike_button_base)
                    }
                })
                holder.dislikeButton.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(p0: View?) {

                        if (likesList?.contains(myNickname) == true) {
                            likesList!!.remove(myNickname)
                            post.likes = likesList!!.size
                            if (likesList!!.size == 0) {
                                var app = ArrayList<String>()
                                app.add("")
                                dbRefNotificheLikes!!.setValue(app)
                                likesList!!.add("ok")
                            }
                        }

                        if (dislikesList?.contains(myNickname) == false) {
                            if (dislikesList?.get(0) == "ok") {
                                dislikesList?.removeAt(0)
                                dislikesList?.add(myNickname!!)
                                if (approvazioni?.minus(1)!! < 0)
                                    dbRefApprovazioni!!.setValue(0)
                                else {
                                    approvazioni = approvazioni?.minus(1)
                                    dbRefApprovazioni!!.setValue(approvazioni)
                                }
                            } else {
                                dislikesList?.add(myNickname!!)
                                if (approvazioni?.minus(1)!! < 0)
                                    dbRefApprovazioni!!.setValue(0)
                                else {
                                    approvazioni = approvazioni?.minus(1)
                                    dbRefApprovazioni!!.setValue(approvazioni)
                                }
                            }

                        } else if (dislikesList?.contains(myNickname) == true) {
                            dislikesList?.remove(myNickname)
                            approvazioni = approvazioni?.plus(1)
                            dbRefApprovazioni!!.setValue(approvazioni)
                            if (dislikesList!!.size == 0)
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

                        if(likesList!!.contains(myNickname))
                            holder.likeButton.setBackgroundResource(R.drawable.like_button_pressed)
                        else
                            holder.likeButton.setBackgroundResource(R.drawable.like_button_base)

                        if(dislikesList!!.contains(myNickname))
                            holder.dislikeButton.setBackgroundResource(R.drawable.dislike_button_pressed)
                        else
                            holder.dislikeButton.setBackgroundResource(R.drawable.dislike_button_base)
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
        val proPic=itemView.userImg
        val postUser = itemView.post_user
        val postCategory = itemView.post_category
        val postDate = itemView.post_data
        var postLike = itemView.post_like
        var postDislike = itemView.post_dislike
        val likeButton = itemView.btnLike
        val dislikeButton = itemView.btnDislike
        val commentButton = itemView.post_comment
        val shareButton = itemView.buttonShare
        lateinit var dataPost: String
        val cUser = FirebaseAuth.getInstance().currentUser!!.uid
        val valoreHash = cUser.hashCode().absoluteValue
        val uId = "anonym$valoreHash"
        var likesList: ArrayList<String> = arrayListOf()
        var dislikesList: ArrayList<String> = arrayListOf()
        var dbRefNickname = FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("InfoUtenti").child("$uId").child("nickname")
        lateinit var myNickname: String

        fun bind(post: Post2) {
            val requestOptionsForPosts = com.bumptech.glide.request.RequestOptions()
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.loading_placeholder)

            val requestOptionsForProfilePicture = com.bumptech.glide.request.RequestOptions()
                .placeholder(R.drawable.anonym_icon)
                .error(R.drawable.anonym_icon)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptionsForPosts).load(post.image)
                .into(postImage)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptionsForProfilePicture).load(post.proPic)
                .into(proPic)


            if(post.nickname==null)
                postUser.setText(post.user)
            else
                postUser.setText(post.nickname)
            postCategory.setText(post.category)

            var postName = post.date
            if (postName != null) {
                var array = postName.split("_")
                dataPost = array[0] + "/" + array[1] + "/" + array[2]
            }
            postDate.setText(dataPost)

            postLike.setText(post.likes.toString())
            postDislike.setText(post.dislikes.toString())

            dbRefNickname.get().addOnCompleteListener {
                if(it.isSuccessful){
                    myNickname = it.result.value as String
                }
            }
            val likeReference =
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti").child(post.user!!).child(post.date!!).child("arrayLikes")
            likeReference.get().addOnCompleteListener {
                likesList = it.result.value as ArrayList<String>
                if(likesList.contains(myNickname))
                    likeButton.setBackgroundResource(R.drawable.like_button_pressed)
                else
                    likeButton.setBackgroundResource(R.drawable.like_button_base)
            }

            val dislikeReference =
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(post.user!!).child(post.date!!).child("arrayDislikes")
            dislikeReference.get().addOnCompleteListener {
                dislikesList = it.result.value as ArrayList<String>
                if(dislikesList.contains(myNickname))
                    dislikeButton.setBackgroundResource(R.drawable.dislike_button_pressed)
                else
                    dislikeButton.setBackgroundResource(R.drawable.dislike_button_base)
            }
        }
    }
}