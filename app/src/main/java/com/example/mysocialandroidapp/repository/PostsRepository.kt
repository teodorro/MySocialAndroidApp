package com.example.mysocialandroidapp.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp.dto.Media
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.enumeration.AttachmentType
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    var userId: Long
    val allPosts: Flow<PagingData<Post>>
    val userPosts: Flow<PagingData<Post>>
    fun getNewerCount(postId: Long): Flow<Int>
    suspend fun getAllPosts()
    suspend fun getUserPosts(userId: Long)
    suspend fun likeById(postId: Long)
    suspend fun likeById(userId: Long, postId: Long)
    suspend fun updateWasSeen()
    suspend fun removeWork(postId: Long)
    suspend fun save(post: Post)
    suspend fun saveWork(post: Post, upload: MediaUpload?): Long
    suspend fun processWork(postId: Long, attachmentType: AttachmentType?)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun clearLocalTable()
    val allUsers: Flow<List<User>>
    suspend fun getUsers()
}