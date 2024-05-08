package br.com.borgesprojetos.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
