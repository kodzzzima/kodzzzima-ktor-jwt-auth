package ru

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database
import ru.data.user.ExposedUserDataSource
import ru.data.user.UserDataSource
import ru.plugins.configureMonitoring
import ru.plugins.configureRouting
import ru.plugins.configureSerialization
import ru.security.configureSecurity
import ru.security.hashing.HashingService
import ru.security.hashing.SHA256HashingService
import ru.security.token.JwtTokenService
import ru.security.token.TokenConfig
import ru.security.token.TokenService

fun main() {
    Database.connect(
        url = "jdbc:postgresql://127.0.0.1:5432/kodzzzima-jwt",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "1234"
    )

    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "jwt-audience"
    val jwtDomain =  "http://127.0.0.1:8080"
    val jwtRealm = "ktor sample app"
    val tokenConfig = TokenConfig(
        issuer = jwtDomain,
        audience = jwtAudience,
        realm = jwtRealm,
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val userDataSource: UserDataSource = ExposedUserDataSource()
    val hashingService: HashingService = SHA256HashingService()
    val tokenService: TokenService = JwtTokenService()

    configureSecurity(tokenConfig)
    configureSerialization()
    configureMonitoring()
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}
