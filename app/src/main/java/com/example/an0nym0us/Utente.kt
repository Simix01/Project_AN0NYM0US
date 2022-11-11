package com.example.an0nym0us

data class Utente(
    val proPic: String,
    val nickname: String,
    val approvazioni: Int,
    val canEdit: Boolean,
    val canBeFound: Boolean
)
