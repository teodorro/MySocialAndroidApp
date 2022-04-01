package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.dto.Job
import com.example.mysocialandroidapp.model.JobsFeedModel
import com.example.mysocialandroidapp.model.JobsFeedModelState
import com.example.mysocialandroidapp.model.PostFeedModelState
import com.example.mysocialandroidapp.repository.JobsRepository
import com.example.mysocialandroidapp.repository.UsersRepository
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnotherUserJobsViewModel @Inject constructor(
    private val repository: JobsRepository,
    private val usersRepository: UsersRepository,
    private val workManager: WorkManager,
    val appAuth: AppAuth,
) : ViewModel()  {
    var userId: Long = 0
        set(value) {
            field = value
            viewModelScope.launch {
                usersRepository.data.collect{ x ->
                    var user = x.firstOrNull() { y -> y.id == userId }
                    _username.value = user?.name
                    _avatar.value = user?.avatar
                }
            }
            viewModelScope.launch {
                repository.clearLocalTable()
            }
            repository.userId = value
            viewModelScope.launch {
                repository.getJobs(userId)
            }
        }

    private val _username = MutableLiveData("")
    val username: LiveData<String>
        get() = _username

    private val _avatar = MutableLiveData<Any?>(null)
    val avatar: LiveData<Any?>
        get() = _avatar

    private val _edited = MutableLiveData(emptyJob)
    val edited: LiveData<Job>
        get() = _edited

    private val _dataState = MutableLiveData<JobsFeedModelState>()
    val dataState: LiveData<JobsFeedModelState>
        get() = _dataState

    val data: LiveData<JobsFeedModel> = repository.data
        .map { jobs ->
            JobsFeedModel(
                jobs,
                jobs.isEmpty()
            )
        }.asLiveData()


    fun loadJobs() = viewModelScope.launch {
        try {
            _dataState.value = JobsFeedModelState(loading = true)
            repository.getJobs(userId)
//            repository.updateWasSeen()
            _dataState.value = JobsFeedModelState()
        } catch (e: Exception) {
            _dataState.value = JobsFeedModelState(error = true)
        }
    }

//    private val _jobsFeed = MutableLiveData<JobsFeedModel>()
//    val jobsFeed: LiveData<JobsFeedModel>
//        get() = _jobsFeed
//
//    init {
//        loadJobs(appAuth.authStateFlow.value!!.id)
//    }
//
//    fun loadJobs(userId: Long) {
//        val jobs = Samples.getJobs(userId)
//        _jobsFeed.value = JobsFeedModel(jobs, jobs?.isEmpty())
//    }
}