package com.example.an0nym0us

import android.os.Parcel
import android.os.Parcelable

data class Post(val user:String,
                val category:String,
                val date:String,
                val image:String,
                val likes:String,
                val dislikes:String ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user)
        parcel.writeString(category)
        parcel.writeString(date)
        parcel.writeString(image)
        parcel.writeString(likes)
        parcel.writeString(dislikes)
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

