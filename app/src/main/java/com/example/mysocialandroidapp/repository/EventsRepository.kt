package com.example.mysocialandroidapp.repository

import androidx.paging.PagingData
import com.example.mysocialandroidapp.dto.*
import kotlinx.coroutines.flow.Flow

interface EventsRepository {
//    val data: Flow<PagingData<Event>>
    val data: Flow<List<Event>>
    fun getNewerCount(eventId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun save(event: Event)
    suspend fun removeById(eventId: Long)
    suspend fun likeById(eventId: Long)
    suspend fun participateEventById(eventId: Long) // or withdraw
    suspend fun saveWithAttachment(event: Event, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(event: Event, upload: MediaUpload?): Long
    suspend fun processWork(eventId: Long)
    suspend fun removeWork(eventId: Long)
    suspend fun clearLocalTable()
    val allUsers: Flow<List<User>>
    suspend fun getUsers()
}