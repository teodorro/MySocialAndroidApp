package com.example.mysocialandroidapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mysocialandroidapp.repository.JobsRepository
import javax.inject.Inject
import javax.inject.Singleton

class RemoveJobWorker (
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: JobsRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val JOB_KEY = "job"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(JOB_KEY, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        return try {
            repository.removeJob(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @Singleton
    class Factory @Inject constructor(
        private val repository: JobsRepository,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = when (workerClassName) {
            RemoveJobWorker::class.java.name ->
                RemoveJobWorker(appContext, workerParameters, repository)
            else ->
                null
        }
    }
}