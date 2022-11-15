package com.example.an0nym0us

data class Commento(
    var user: String,
    var content: String
) {
    operator fun get(s: String): Any {
        return user
    }
}