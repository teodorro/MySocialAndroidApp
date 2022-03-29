package com.example.mysocialandroidapp.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp.dto.Media
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.dto.User
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<PagingData<Post>>
    fun getNewerCount(postId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun removeById(postId: Long)
    suspend fun likeById(postId: Long)
    suspend fun updateWasSeen()
    suspend fun removeWork(postId: Long)
    suspend fun clearLocalTable()
//    val allUsers: Flow<List<User>>
//    suspend fun getUsers()
}