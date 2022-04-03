package com.example.mysocialandroidapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mysocialandroidapp.repository.PostsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class RefreshPostsWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: PostsRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val NAME = "com.example.work.RefreshPostsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getAllPosts()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    @Singleton
    class Factory @Inject constructor(
        private val repository: PostsRepository,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = when (workerClassName) {
            RefreshPostsWorker::class.java.name ->
                RefreshPostsWorker(appContext, workerParameters, repository)
            else ->
                null
        }
    }
}