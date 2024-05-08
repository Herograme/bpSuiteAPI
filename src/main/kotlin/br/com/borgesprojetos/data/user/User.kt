package br.com.borgesprojetos.data.user



data class User (
    val username: String,
    val password: String,
    val salt: String,
    val role: Int = 1,
    val id:Int?
)
