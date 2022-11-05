package com.example.an0nym0us

data class Post2(
    var date: String?= null
    , var image: String?=null
    , var dislikes: Int
    , var category:String?=null
    , var user:String?=null
    , var likes:Int
    , var comments: ArrayList<Commento>? = null
    , var arrayLikes: ArrayList<String>? = null
    , var arrayDislikes: ArrayList<String>? = null)
