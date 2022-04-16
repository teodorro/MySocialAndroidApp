package com.example.mysocialandroidapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.repository.PostsRepository
import javax.inject.Inject
import javax.inject.Singleton

class SavePostWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: PostsRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val POST_KEY = "post"
        const val ATTACHMENT_TYPE = "attachmentType"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(POST_KEY, 0L)
        val atType = inputData.getInt(ATTACHMENT_TYPE, 0)
        if (id == 0L) {
            return Result.failure()
        }
        val attachmentType = if (atType != 0) AttachmentType.fromInt(atType) else null
        return try {
            repository.processWork(id, attachmentType)
            Result.success()
        } catch (e: Exception) {
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
            SavePostWorker::class.java.name ->
                SavePostWorker(appContext, workerParameters, repository)
            else ->
                null
        }
    }
}