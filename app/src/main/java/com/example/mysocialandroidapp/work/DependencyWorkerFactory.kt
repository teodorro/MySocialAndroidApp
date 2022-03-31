package com.example.mysocialandroidapp.work

import androidx.work.DelegatingWorkerFactory
import com.example.mysocialandroidapp.repository.JobsRepository
import com.example.mysocialandroidapp.repository.PostRepository
import com.example.mysocialandroidapp.repository.WallRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyWorkerFactory @Inject constructor(
    postRepository: PostRepository,
    wallRepository: WallRepository,
    jobRepository: JobsRepository,
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorker.Factory(postRepository))
        addFactory(SavePostWorker.Factory(wallRepository))
        addFactory(RemovePostWorker.Factory(postRepository))
        addFactory(RemoveJobWorker.Factory(jobRepository))
        addFactory(SaveJobWorker.Factory(jobRepository))
    }
}