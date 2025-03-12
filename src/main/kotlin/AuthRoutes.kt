package ru

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.data.user.model.User
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ru.data.requests.AuthRequest
import ru.data.responses.AuthResponse
import ru.data.user.UserDataSource
import ru.security.hashing.HashingService
import ru.security.hashing.SaltedHash
import ru.security.token.TokenClaim
import ru.security.token.TokenConfig
import ru.security.token.TokenService
import java.util.UUID

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = call.receiveNullable<AuthRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        if (areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            id = UUID.randomUUID().toString(),
            userName = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        val wasAcknowledged = userDataSource.insertUser(user)

        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    tokenService: TokenService,
    userDataSource: UserDataSource,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<AuthRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Неправильный логин или пароль")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Неправильный логин или пароль")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(
                HttpStatusCode.OK,
            )
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(
                HttpStatusCode.OK,
                "Your userId is $userId"
            )
        }
    }
}