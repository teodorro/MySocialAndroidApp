package com.example.mysocialandroidapp.repository

import com.example.mysocialandroidapp.dto.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    val data: Flow<List<User>>
    suspend fun getUsers()
    suspend fun clearLocalTable()
}