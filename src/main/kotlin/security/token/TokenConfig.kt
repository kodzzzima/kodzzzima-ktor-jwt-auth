package ru.security.token

/**
 * @param issuer Тот кто выпустил токен. В нашем случае это сервер
 * @param audience Может быть обычный пользователь или с правами админа
 * @param expiresIn Дата истечения срока действия
 * @param secret Клиент не должен о нем знать
 *
 */
data class TokenConfig (
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String,
    val realm: String,
)
