package ru.security.token

/**
 * Класс без состояния, который предоставляет функционал для других классов
 */
interface TokenService {

    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}