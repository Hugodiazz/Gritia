package com.devdiaz.gritia.data.repository

import com.devdiaz.gritia.di.IoDispatcher
import com.devdiaz.gritia.model.User
import com.devdiaz.gritia.model.dao.UserDao
import com.devdiaz.gritia.model.dto.toDto
import com.devdiaz.gritia.model.mappers.toDomain
import com.devdiaz.gritia.model.mappers.toEntity
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun getUserByEmail(email: String): User?
    suspend fun saveUser(user: User)
    suspend fun clearUserData()
}

class UserRepositoryImpl
@Inject
constructor(
        private val userDao: UserDao,
        private val postgrest: Postgrest,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override fun getUser(): Flow<User?> = userDao.getUser().map { it?.toDomain() }

    override suspend fun getUserByEmail(email: String): User? =
            withContext(ioDispatcher) { userDao.getUserByEmail(email)?.toDomain() }

    override suspend fun saveUser(user: User) =
            withContext(ioDispatcher) {
                // 1. Local Update
                if (user.id == 0L) {
                    userDao.insertUser(user.toEntity())
                } else {
                    userDao.updateUser(user.toEntity())
                }

                // 2. Remote Update (Supabase)
                try {
                    postgrest.from("users").update(user.toDto()) {
                        filter { eq("email", user.email) }
                    }
                } catch (e: Exception) {}
                Unit
            }

    override suspend fun clearUserData() =
            withContext(ioDispatcher) {
                userDao.deleteAllUsers()
            }
}
