package com.example.mysocialandroidapp.work

import androidx.work.DelegatingWorkerFactory
import com.example.mysocialandroidapp.repository.PostRepository
import com.example.mysocialandroidapp.repository.WallRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyWorkerFactory @Inject constructor(
    postRepository: PostRepository,
    wallRepository: WallRepository,
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorker.Factory(postRepository))
        addFactory(SavePostWorker.Factory(wallRepository))
        addFactory(RemovePostWorker.Factory(postRepository))
    }
}