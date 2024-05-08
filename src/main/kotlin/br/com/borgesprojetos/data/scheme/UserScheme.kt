package br.com.borgesprojetos.data.scheme

import br.com.borgesprojetos.data.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.sql.Statement
import java.sql.Connection as SqlConnection


class UserService(private val connection: SqlConnection) {
    companion object {
        private const val SELECT_USERS_BY_USERNAME = "SELECT * FROM users WHERE user_name = ?"
        private const val SELECT_USERS_BY_ID = "SELECT * FROM users WHERE user_id = ?"
        private const val INSERT_USERS = "INSERT INTO users (user_name,password,user_role,salt) VALUES (?,?,?,?)"
        private const val UPDATE_USERS = "UPDATE users SET " +
                "user_id = ?, " +
                "user_name = ?, " +
                "password = ?, " +
                "user_role = ?" +
                "salt = ?"
        private const val DELETE_USERS = "DELETE FROM users WHERE user_id = ?"
    }

    init {
        try {
            val reader = BufferedReader(FileReader("src/main/resources/schemes/user.sql"))
            val statement = connection.createStatement()

            val sqlScript = reader.readText()
            println(sqlScript)
            val result = statement.executeUpdate(sqlScript)
            println(result)
            statement.close()


            println("Script SQL executado com sucesso.")
        } catch (e: Exception) {
            println("Erro ao executar o script SQL: ${e.message}")
        }
    }

    suspend fun selectUserByID(id:Int):User = withContext(Dispatchers.IO){
        println("trying finding by Id")
        val statement = connection.prepareStatement(SELECT_USERS_BY_ID)
        statement.setInt(1, id)
        val result = statement.executeQuery()

        if (result.next()) {

            val userid = result.getString("user_id")
            val username = result.getString("user_name")
            val password = result.getString("password")
            val salt = result.getString("salt")
            val role = result.getString("user_role")


            return@withContext User(username,password,salt,role.toInt(),userid.toInt())
        } else {
            throw Exception("Record not found")
        }
    }

    suspend fun selectUserByUsername(username: String): User = withContext(Dispatchers.IO) {
        println("trying finding by name")
        val statement = connection.prepareStatement(SELECT_USERS_BY_USERNAME)
        statement.setString(1, username)
        val result = statement.executeQuery()

        println(result)

        if (result.next()) {
            val userid = result.getString("user_id")
            val username = result.getString("user_name")
            val password = result.getString("password")
            val salt = result.getString("salt")
            val role = result.getString("user_role")

            //println("Id:$userid,name:$username,password:$password,salt:$salt,role:$role")
            return@withContext User(
                id = userid.toInt(),
                username = username,
                password = password,
                role = role.toInt(),
                salt = salt
            )
        } else {
            throw Exception("Record not found")
        }

    }

    suspend fun insertUser(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USERS, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.username)
        statement.setString(2, user.password)
        statement.setInt(3, user.role)
        statement.setString(4, user.salt)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Indisponível para criar o ID para o usuário inserido.")
        }
    }

    suspend fun updateUser(userScheme: User):Boolean = withContext(Dispatchers.IO) {
        userScheme.id?.let {
            val statement = connection.prepareStatement(UPDATE_USERS)
            statement.setInt(1, userScheme.id)
            statement.setString(2, userScheme.username)
            statement.setString(3, userScheme.password)
            statement.setInt(4, userScheme.role)
            statement.setString(5,userScheme.salt)

            statement.executeUpdate()
            return@withContext true
        }
        return@withContext false
    }

    suspend fun deleteUser(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USERS)
        statement.setInt(1, id)
        statement.executeUpdate()
    }


}




