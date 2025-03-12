package ru.data.database

import org.jetbrains.exposed.sql.Table

object Users : Table(name = "users") {
    val id = varchar("id", 255).uniqueIndex()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255)
    val salt = varchar("salt", 255)
}