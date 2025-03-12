package ru.data.user

import ru.data.user.model.User

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User): Boolean
}