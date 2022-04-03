package com.example.mysocialandroidapp.work

import androidx.work.DelegatingWorkerFactory
import com.example.mysocialandroidapp.repository.EventsRepository
import com.example.mysocialandroidapp.repository.JobsRepository
import com.example.mysocialandroidapp.repository.PostsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyWorkerFactory @Inject constructor(
    postsRepository: PostsRepository,
    jobRepository: JobsRepository,
    eventsRepository: EventsRepository,
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorker.Factory(postsRepository))
        addFactory(SavePostWorker.Factory(postsRepository))
        addFactory(RemovePostWorker.Factory(postsRepository))
        addFactory(RemoveJobWorker.Factory(jobRepository))
        addFactory(SaveJobWorker.Factory(jobRepository))
        addFactory(RefreshEventsWorker.Factory(eventsRepository))
        addFactory(SaveEventWorker.Factory(eventsRepository))
        addFactory(RemoveEventWorker.Factory(eventsRepository))
    }
}