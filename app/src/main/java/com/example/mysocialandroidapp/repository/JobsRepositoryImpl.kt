package com.example.mysocialandroidapp.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.dao.*
import com.example.mysocialandroidapp.dto.Job
import com.example.mysocialandroidapp.entity.*
import com.example.mysocialandroidapp.error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class JobsRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: DataApiService,
) : JobsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override var userId: Long = 0
        set(value) {
            field = value
        }

    override val data = jobDao.getAll()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getJobs(userId: Long) {
        try {
            // получить все работы с сервера
            val response = apiService.getJobs(userId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveJob(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun processWork(jobId: Long) {
        try {
            val entity = jobDao.getById(jobId)
            var job = entity.toDto()
            saveJob(job)

            Log.d(null, entity.id.toString())
            Log.d(null, job.id.toString())
        } catch (e: Exception) {
            Log.d(null, e.message.toString())
            throw UnknownError
        }
    }

    override suspend fun removeJob(jobId: Long) {
        try {
            val response = apiService.removeJobById(jobId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            jobDao.removeById(jobId)

        } catch (e: Exception) {
            var a = e
            throw UnknownError
        }
    }

    override suspend fun clearLocalTable() {
        try{
            jobDao.removeAll()
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}