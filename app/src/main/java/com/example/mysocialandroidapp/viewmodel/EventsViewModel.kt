package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.model.EventsFeedModel
import com.example.mysocialandroidapp.model.PostsFeedModel
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
) : ViewModel() {
    private val _eventsFeed = MutableLiveData<EventsFeedModel>()
    val eventsFeed: LiveData<EventsFeedModel>
        get() = _eventsFeed

    var userId: Long = 0

    init {
        loadEvents(AppAuth.currentAuthorId)
    }

    fun loadEvents(userId: Long) {
        val eventsFeed = Samples.getEvents()
        _eventsFeed.value = EventsFeedModel(eventsFeed, eventsFeed?.isEmpty())
    }
}