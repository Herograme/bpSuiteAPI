package br.com.borgesprojetos.data.user


import br.com.borgesprojetos.data.scheme.UserService

import java.sql.Connection

class PostgreUserDataSource(private val db: Connection):UserDataSource {
    private val userService = UserService(db)


    override suspend fun getUserByUsername(username: String):User{
        val user = userService.selectUserByUsername(username)
        //println(user)
        return  user
    }

    override suspend fun insertUser(user: User): Boolean {
        val resultState = false
       try {
           userService.insertUser(user)
           return true
       } catch (e: Exception){
           println("User not inserted:$e")
       }
        return resultState
    }
}