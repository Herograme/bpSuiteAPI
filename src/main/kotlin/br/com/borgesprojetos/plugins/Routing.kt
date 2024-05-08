package br.com.borgesprojetos.plugins

import br.com.borgesprojetos.authenticate
import br.com.borgesprojetos.data.user.UserDataSource
import br.com.borgesprojetos.getSecretInfo
import br.com.borgesprojetos.security.hashing.HashingService
import br.com.borgesprojetos.security.token.TokenConfig
import br.com.borgesprojetos.security.token.TokenService
import br.com.borgesprojetos.singIn
import br.com.borgesprojetos.singUp
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig

) {
    routing {
        singIn(userDataSource,hashingService,tokenService,tokenConfig)
        singUp(hashingService,userDataSource)
        authenticate()
        getSecretInfo()
    }
}
