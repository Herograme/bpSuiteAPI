package br.com.borgesprojetos

import br.com.borgesprojetos.data.user.PostgreUserDataSource
import br.com.borgesprojetos.plugins.*
import br.com.borgesprojetos.security.hashing.SHA256HashingService
import br.com.borgesprojetos.security.token.JwtTokenService
import br.com.borgesprojetos.security.token.TokenConfig
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module() {
    configureSerialization()
    val connection = configureDatabases()


    val userDataSource = PostgreUserDataSource(connection)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        //secret = System.getenv("JWT_SECRET")
        secret = environment.config.property("jwt.secret").getString()
    )
    val hashingService = SHA256HashingService()


    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource,hashingService,tokenService,tokenConfig)
}
