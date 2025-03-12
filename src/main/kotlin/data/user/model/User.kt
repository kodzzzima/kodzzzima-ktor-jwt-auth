package ru.data.user.model

data class User(
    val id: String,
    val userName: String,
    val password: String,
    val salt: String,
)
