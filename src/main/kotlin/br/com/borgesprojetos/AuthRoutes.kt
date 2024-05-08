package br.com.borgesprojetos

import br.com.borgesprojetos.data.requests.AuthRequest
import br.com.borgesprojetos.data.responses.AuthResponse
import br.com.borgesprojetos.data.user.User
import br.com.borgesprojetos.data.user.UserDataSource
import br.com.borgesprojetos.security.hashing.HashingService
import br.com.borgesprojetos.security.hashing.SaltedHash
import br.com.borgesprojetos.security.token.TokenClaim
import br.com.borgesprojetos.security.token.TokenConfig
import br.com.borgesprojetos.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.singUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
) {

    post("signup") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
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
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt,
            id = null
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if(!wasAcknowledged){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.singIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){

    post("signin") {

        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {

            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        println("receive request $request ")

        val user = userDataSource.getUserByUsername(request.username)
        if(user == null){
            println("1")
            call.respond(HttpStatusCode.Conflict,"Username ou Senha incorretos")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if(!isValidPassword) {
            println("2")
            call.respond(HttpStatusCode.Conflict,"Senha incorretos")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
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
    authenticate{
       get("authenticate"){
           call.respond(HttpStatusCode.OK)
       }
    }
}

fun Route.getSecretInfo(){
    authenticate{
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId",String::class)
            call.respond(HttpStatusCode.OK,"Your userId is $userId")
        }
    }
}