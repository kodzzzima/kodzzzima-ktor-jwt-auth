package ru.security.token

/**
 * Класс для хранения пар ключ значение
 * @param name Имя пользователя
 * @param value
 */
data class TokenClaim(
    val name: String,
    val value: String,
)