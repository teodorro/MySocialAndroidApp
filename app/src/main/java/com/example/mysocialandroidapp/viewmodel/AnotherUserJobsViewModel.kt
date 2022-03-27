package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.model.JobsFeedModel
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnotherUserJobsViewModel @Inject constructor(
    val appAuth: AppAuth,
) : ViewModel()  {
    private val _jobsFeed = MutableLiveData<JobsFeedModel>()
    val jobsFeed: LiveData<JobsFeedModel>
        get() = _jobsFeed

    var userId: Long = 0

    init {
        loadJobs(appAuth.authStateFlow.value!!.id)
    }

    fun loadJobs(userId: Long) {
        val jobs = Samples.getJobs(userId)
        _jobsFeed.value = JobsFeedModel(jobs, jobs?.isEmpty())
    }
}