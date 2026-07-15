package com.dadaschatpos.data.repository

import com.dadaschatpos.data.dao.UserDao
import com.dadaschatpos.data.model.UserEntity
import com.dadaschatpos.util.PasswordHasher
import com.dadaschatpos.util.SessionManager

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    suspend fun register(
        name: String,
        shopName: String,
        mobile: String,
        email: String,
        password: String
    ): Result<UserEntity> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        require(userDao.findByEmail(normalizedEmail) == null) { "Email already registered" }
        val user = UserEntity(
            name = name.trim(),
            shopName = shopName.trim(),
            mobile = mobile.trim(),
            email = normalizedEmail,
            password = PasswordHasher.hash(password)
        )
        val id = userDao.insert(user)
        user.copy(id = id)
    }

    suspend fun login(email: String, password: String, remember: Boolean): Result<UserEntity> = runCatching {
        val user = userDao.findByEmail(email.trim().lowercase()) ?: error("User not found")
        require(user.password == PasswordHasher.hash(password)) { "Invalid password" }
        sessionManager.saveSession(user.id, remember)
        user
    }

    suspend fun currentUser(): UserEntity? = sessionManager.currentUserId?.let { userDao.findById(it) }

    fun logout() = sessionManager.logout()
}
