package com.example.mysocialandroidapp.application

import android.app.Application
import androidx.work.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.samples.Samples2
import com.example.mysocialandroidapp.work.RefreshPostsWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application()  {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var auth: AppAuth
    @Inject
    lateinit var workManager: WorkManager
    @Inject
    lateinit var samples: Samples2


    override fun onCreate() {
        super.onCreate()
        setupAuth()
        setupWork()

        //loadSamples()
    }

    private fun loadSamples() {
        appScope.launch {
            samples.loadSampleData2()
        }
    }

    private fun setupAuth() {
        appScope.launch {
            auth.sendPushToken()
        }
    }

    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(
                RefreshPostsWorker.NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}