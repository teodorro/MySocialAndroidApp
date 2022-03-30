package com.example.mysocialandroidapp.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp.dto.Media
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.dto.User
import kotlinx.coroutines.flow.Flow

interface WallRepository {
    var userId: Long
    val data: Flow<PagingData<Post>>
    suspend fun getAllPosts(userId: Long)
    suspend fun save(post: Post)
    suspend fun saveWork(post: Post, upload: MediaUpload?): Long
    suspend fun processWork(postId: Long)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun likeById(userId: Long, postId: Long)
    suspend fun clearLocalTable()
    val allUsers: Flow<List<User>>
    suspend fun getUsers()
}