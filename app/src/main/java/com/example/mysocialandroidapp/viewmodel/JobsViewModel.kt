package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.*
import androidx.work.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.dto.Job
import com.example.mysocialandroidapp.model.JobsFeedModel
import com.example.mysocialandroidapp.model.JobsFeedModelState
import com.example.mysocialandroidapp.repository.JobsRepository
import com.example.mysocialandroidapp.repository.UsersRepository
import com.example.mysocialandroidapp.samples.Samples
import com.example.mysocialandroidapp.util.SingleLiveEvent
import com.example.mysocialandroidapp.work.RemoveJobWorker
import com.example.mysocialandroidapp.work.SaveJobWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

val emptyJob = Job(
    id = 0,
    name = "",
    position = "",
    start = Instant.now().epochSecond,
    finish = null,
    link = null
)

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val repository: JobsRepository,
    private val usersRepository: UsersRepository,
    private val workManager: WorkManager,
    val appAuth: AppAuth,
) : ViewModel() {

    var userId: Long = 0
        set(value) {
            field = value
            viewModelScope.launch {
                usersRepository.data.collect { x ->
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

    private val _edited = MutableLiveData(emptyJob)
    val edited: LiveData<Job>
        get() = _edited

    private val _dataState = MutableLiveData<JobsFeedModelState>()
    val dataState: LiveData<JobsFeedModelState>
        get() = _dataState

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    private val _username = MutableLiveData("")
    val username: LiveData<String>
        get() = _username

    private val _avatar = MutableLiveData<Any?>(null)
    val avatar: LiveData<Any?>
        get() = _avatar

    val data: LiveData<JobsFeedModel> = repository.data
        .map { jobs ->
            JobsFeedModel(
                jobs,
                jobs.isEmpty()
            )
        }.asLiveData()

    fun save() {
        _edited.value?.let {
            _jobCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveJob(it)
                    val data = workDataOf(SaveJobWorker.JOB_KEY to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SaveJobWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = JobsFeedModelState()
                } catch (e: Exception) {
                    var a = e
                    _dataState.value = JobsFeedModelState(error = true)
                }
            }
        }
        _edited.value = emptyJob
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = JobsFeedModelState(loading = true)

                val data = workDataOf(RemoveJobWorker.JOB_KEY to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemoveJobWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)

                _dataState.value = JobsFeedModelState()
            } catch (e: Exception) {
                _dataState.value = JobsFeedModelState(error = true)
            }
        }
    }

    fun edit(job: Job) {
        _edited.value = job
    }

    fun changeContent(
        name: String,
        position: String,
        start: String,
        finish: String,
        link: String,
    ) {
        val name = name.trim()
        val position = position.trim()
        val start = start.trim().toLong()
        val finish = if (finish.trim().isNullOrBlank()) null else finish.trim()?.toLong()
        val link = link.trim()

        if (
            _edited.value?.name == name
            && _edited.value?.position == position
            && _edited.value?.start == start
            && _edited.value?.finish == finish
            && _edited.value?.link == link
        ) {
            return
        }

        _edited.value = _edited.value?.copy(
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link
        )
    }

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
}