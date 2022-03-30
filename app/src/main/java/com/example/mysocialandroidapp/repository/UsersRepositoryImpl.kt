package com.example.mysocialandroidapp.repository

import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.dao.UserDao
import com.example.mysocialandroidapp.entity.*
import com.example.mysocialandroidapp.error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: DataApiService,
) : UsersRepository {

    override val data = userDao.getAll()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getUsers() {
        try {
            // получить всех пользователей с сервера
            val response = apiService.getUsersAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}