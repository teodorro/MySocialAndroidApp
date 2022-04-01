package com.example.mysocialandroidapp.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.dto.Coordinates
import com.example.mysocialandroidapp.dto.Event
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.enumeration.EventType
import com.example.mysocialandroidapp.model.*
import com.example.mysocialandroidapp.repository.EventsRepository
import com.example.mysocialandroidapp.samples.Samples
import com.example.mysocialandroidapp.util.SingleLiveEvent
import com.example.mysocialandroidapp.work.RemoveEventWorker
import com.example.mysocialandroidapp.work.SaveEventWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import javax.inject.Inject

val emptyEvent = Event(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    authorAvatar = "",
    likedByMe = false,
    published = Instant.now().toString(),
    datetime = Instant.now().toString(),
    coords = null,
    link = null,
    speakerIds = mutableSetOf(),
    participantsIds = emptySet(),
    participatedByMe = false,
    likeOwnerIds = emptySet(),
    attachment = null,
    type = EventType.OFFLINE
)

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: EventsRepository,
    private val workManager: WorkManager,
    val appAuth: AppAuth,
) : ViewModel() {

//    private val cached = repository
//        .data
//        .cachedIn(viewModelScope)
//
//    val data: Flow<PagingData<Event>> = appAuth.authStateFlow
//        .flatMapLatest { (myId, _) ->
//            cached.map { pagingData ->
//                pagingData.map { event ->
//                    //TODO
//                    event.copy()
//                }
//            }
//        }

    val data: LiveData<EventsFeedModel> = repository.data
        .map { events ->
            EventsFeedModel(
                events,
                events.isEmpty()
            )
        }.asLiveData()

    val allUsers: LiveData<UsersFeedModel> = repository.allUsers
        .map { users ->
            UsersFeedModel(
                users,
                users.isEmpty()
            )
        }.asLiveData()

    private val _dataState = MutableLiveData<EventsFeedModelState>()
    val dataState: LiveData<EventsFeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(emptyEvent)
    val edited: LiveData<Event>
        get() = _edited

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = EventsFeedModelState(loading = true)
            repository.getAll()
            _dataState.value = EventsFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventsFeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = EventsFeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = EventsFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventsFeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveWork(
                        it, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SaveEventWorker.EVENT_KEY to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SaveEventWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = EventsFeedModelState()
                } catch (e: Exception) {
                    _dataState.value = EventsFeedModelState(error = true)
                }
            }
        }
        _edited.value = emptyEvent
        _photo.value = noPhoto
    }

    fun edit(event: Event) {
        _edited.value = event
    }

    fun changeContent(content: String,
                      position: String,
                      date: String,
                      link: String,) {
        val content = content.trim()
        val position = position.trim()
        val date = date.trim()
        val link = if (link.isNullOrBlank()) null else link.trim()

        if (_edited.value?.content == content
            && _edited.value?.coords.toString() == position
            && _edited.value?.datetime == date
            && _edited.value?.link == link
        ) {
            return
        }

        val coordsStr = position.trim().split(" ")
        val coordsLong = coordsStr.map { x ->
            try{
                x.trim().toDouble()
            } catch (ex: Exception ){
                0.0
            }}
        val coords = Coordinates(
            if (coordsLong.count() > 0) coordsLong[0] else 0.0,
            if (coordsLong.count() > 1) coordsLong[1] else 0.0)

        _edited.value = edited.value?.copy(
            content = content,
            coords = coords,
            datetime = date,
            link = link,
            author = appAuth.userFlow.value.name,
            authorId = appAuth.userFlow.value.id
        )
    }

    fun likeById(event: Event) {
        viewModelScope.launch {
            try {
                _edited.value = event
                _dataState.value = EventsFeedModelState(loading = true)
                repository.likeById(event.id)
                _edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = EventsFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventsFeedModelState(error = true)
            } finally {
                _edited.value = emptyEvent
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = EventsFeedModelState(loading = true)

                val data = workDataOf(RemoveEventWorker.EVENT_KEY to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemoveEventWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)

                _dataState.value = EventsFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventsFeedModelState(error = true)
            }
        }
    }

    fun participateById(event: Event) {
        viewModelScope.launch {
            try {
                _edited.value = event
                _dataState.value = EventsFeedModelState(loading = true)
                repository.participateEventById(event.id)
                _edited.value = edited.value?.copy(participatedByMe = true)
                _dataState.value = EventsFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventsFeedModelState(error = true)
            } finally {
                _edited.value = emptyEvent
            }
        }
    }

    fun clearLocalTable(){
        viewModelScope.launch {
            try {
                _dataState.value = EventsFeedModelState(loading = true)
                repository.clearLocalTable()
                _dataState.value = EventsFeedModelState()
            } catch (e: Exception) {
                _dataState.value = EventsFeedModelState(error = true)
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

//    val allUsers: LiveData<UsersFeedModel> = repository.allUsers
//        .map { users ->
//            UsersFeedModel(users,
//                users.isEmpty()
//            )
//        }.asLiveData()



//    private val _eventsFeed = MutableLiveData<EventsFeedModel>()
//    val eventsFeed: LiveData<EventsFeedModel>
//        get() = _eventsFeed
//
//    var userId: Long = 0
//
//    init {
//        loadEvents()
//    }
//
//    fun loadEvents() {
//        val eventsFeed = Samples.getEvents()
//        _eventsFeed.value = EventsFeedModel(eventsFeed, eventsFeed?.isEmpty())
//    }
}