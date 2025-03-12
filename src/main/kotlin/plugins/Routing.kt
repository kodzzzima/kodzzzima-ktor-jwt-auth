package ru.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.authenticate
import ru.data.user.UserDataSource
import ru.getSecretInfo
import ru.security.hashing.HashingService
import ru.security.token.TokenConfig
import ru.security.token.TokenService
import ru.signIn
import ru.signUp

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, tokenService, userDataSource, tokenConfig)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
