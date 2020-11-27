package com.explosion204.battleship.data.models

data class User(
    val userId: String,
    val nickname: String,
    val profileImageUri: String
        = "https://firebasestorage.googleapis.com/v0/b/battleship-3a89c.appspot.com/o/profileImages%2Fdefault.jpeg?alt=media&token=1452cf9a-b2ea-4ef9-9966-7addeed81d26"
)