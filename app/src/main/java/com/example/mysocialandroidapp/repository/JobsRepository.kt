package com.example.mysocialandroidapp.repository

import com.example.mysocialandroidapp.dto.Job
import kotlinx.coroutines.flow.Flow

interface JobsRepository {
    var userId: Long
    val data: Flow<List<Job>>
    suspend fun getJobs(userId: Long)
    suspend fun saveJob(job: Job)
    suspend fun processWork(jobId: Long)
    suspend fun removeJob(jobId: Long)
    suspend fun clearLocalTable()
}