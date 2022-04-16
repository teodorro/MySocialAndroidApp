package com.example.mysocialandroidapp.observers

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.mysocialandroidapp.R
import com.google.android.material.button.MaterialButton

class MediaLifecycleObserver(private val context: Context) : LifecycleObserver {
    var player: MediaPlayer? = MediaPlayer()
    var currentUrl: String = ""
    var trackButton: MaterialButton? = null

    fun onPlay() {
        player?.start()
        trackButton?.icon = AppCompatResources.getDrawable(
            context,
            R.drawable.ic_baseline_pause_circle_32
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        player?.pause()
        trackButton?.icon = AppCompatResources.getDrawable(
            context,
            R.drawable.ic_baseline_play_circle_32
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        player?.stop()
        player?.reset()
        trackButton?.icon = AppCompatResources.getDrawable(
            context,
            R.drawable.ic_baseline_play_circle_32
        )
    }

}