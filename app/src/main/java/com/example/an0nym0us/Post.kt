package com.example.an0nym0us

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.tasks.Task

data class Post(
    val user:String,
    val category:String,
    val date:String,
    val image: String,
    var likes:Int,
    var dislikes:Int ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user)
        parcel.writeString(category)
        parcel.writeString(date)
        parcel.writeString(image)
        parcel.writeInt(likes)
        parcel.writeInt(dislikes)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Post(user='$user',category='$category',date='$date',image='$image',likes='$likes',dislikes='$dislikes')"
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}

