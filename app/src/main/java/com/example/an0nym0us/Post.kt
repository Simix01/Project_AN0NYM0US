package com.example.an0nym0us

data class Post (
    var user:String,
    var category:String,
    var date:String,
    var image:String
        ) {
    override fun toString(): String {
        return "Post(user='$user',category='$category',date='$date',image='$image')"
    }
}

