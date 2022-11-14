package com.example.an0nym0us

data class Utente(
    var proPic: String,
    var nickname: String,
    var approvazioni: Int,
    var canEdit: Boolean,
    var canBeFound: Boolean,
    var seguiti: ArrayList<String>
)
