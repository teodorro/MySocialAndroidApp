package com.example.mysocialandroidapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mysocialandroidapp.repository.PostRepository
import com.example.mysocialandroidapp.repository.WallRepository
import javax.inject.Inject
import javax.inject.Singleton

class SavePostWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: WallRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val POST_KEY = "post"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(POST_KEY, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        return try {
            repository.processWork(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @Singleton
    class Factory @Inject constructor(
        private val repository: WallRepository,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = when (workerClassName) {
            SavePostWorker::class.java.name ->
                SavePostWorker(appContext, workerParameters, repository)
            else ->
                null
        }
    }
}