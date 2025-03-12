package ru.data.user

import ru.data.user.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.data.database.Users
import ru.data.database.Users.password
import ru.data.database.Users.salt
import ru.data.database.Users.username

class ExposedUserDataSource : UserDataSource {

    override suspend fun getUserByUsername(username: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.username eq username }
                .map(::mapUser)
                .singleOrNull()
        }
    }

    private fun mapUser(row: ResultRow) = User(
        row[Users.id],
        row[username],
        row[password],
        row[salt]
    )

    override suspend fun insertUser(user: User): Boolean {
        return transaction {
            try {
                Users.insert {
                    it[id] = user.id
                    it[username] = user.userName
                    it[password] = user.password
                    it[salt] = user.salt
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}

