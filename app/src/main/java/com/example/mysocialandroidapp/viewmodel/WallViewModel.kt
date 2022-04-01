package com.example.mysocialandroidapp.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import androidx.work.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.model.PhotoModel
import com.example.mysocialandroidapp.model.PostFeedModelState
import com.example.mysocialandroidapp.model.UsersFeedModel
import com.example.mysocialandroidapp.repository.WallRepository
import com.example.mysocialandroidapp.util.SingleLiveEvent
import com.example.mysocialandroidapp.work.RemovePostWorker
import com.example.mysocialandroidapp.work.SavePostWorker
import com.example.mysocialandroidapp.util.DateStringFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.util.*
import javax.inject.Inject


val emptyPost = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    authorAvatar = "",
    likedByMe = false,
    published = Instant.now().toString(),
//    published = DateStringFormatter.getSimpleFromInstance(Instant.now().toString()),
    coords = null,
    link = null,
    mentionIds = mutableSetOf(),
    mentionedMe = false,
    likeOwnerIds = emptySet(),
    attachment = null
)

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WallViewModel @Inject constructor(
    private val repository: WallRepository,
    private val workManager: WorkManager,
    val appAuth: AppAuth,
) : ViewModel() {
    var userId: Long = 0
        set(value) {
            field = value
            viewModelScope.launch {
                repository.clearLocalTable()
            }
            repository.userId = value
            viewModelScope.launch {
                repository.getAllPosts(userId)
            }
        }

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    private val _dataState = MutableLiveData<PostFeedModelState>()
    val dataState: LiveData<PostFeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(emptyPost)
    val edited: LiveData<Post>
        get() = _edited

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    //    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
//        .flatMapLatest { (myId, _) ->
//            cached.map { pagingData ->
//                pagingData.map { post ->
//                    //TODO
//                    post.copy()//ownedByMe = post.authorId == myId)
//                }
//            }
//        }
    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    //TODO
                    post.copy()
                }
            }
        }

    val allUsers: LiveData<UsersFeedModel> = repository.allUsers
        .map { users ->
            UsersFeedModel(
                users,
                users.isEmpty()
            )
        }.asLiveData()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo



    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostFeedModelState(loading = true)
            repository.getAllPosts(userId)
//            repository.updateWasSeen()
            _dataState.value = PostFeedModelState()
        } catch (e: Exception) {
            _dataState.value = PostFeedModelState(error = true)
        }
    }

    fun edit(post: Post) {
        _edited.value = post
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostFeedModelState(refreshing = true)
            repository.getAllPosts(userId)
//            repository.updateWasSeen()
            _dataState.value = PostFeedModelState()
        } catch (e: Exception) {
            _dataState.value = PostFeedModelState(error = true)
        }
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                _edited.value = post
                _dataState.value = PostFeedModelState(loading = true)
                repository.likeById(userId, post.id)
                _edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
            } finally {
                _edited.value = emptyPost
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = PostFeedModelState(loading = true)

                val data = workDataOf(RemovePostWorker.POST_KEY to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)

                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
            }
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveWork(
                        it, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SavePostWorker.POST_KEY to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = PostFeedModelState()
                } catch (e: Exception) {
                    _dataState.value = PostFeedModelState(error = true)
                }
            }
        }
        _edited.value = emptyPost
        _edited.value?.let { it ->
            it.copy(mentionIds = mutableSetOf(), mentionedMe = false) }
        _photo.value = noPhoto
    }


    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        _edited.value = edited.value?.copy(content = text, author = appAuth.userFlow.value.name, authorId = appAuth.userFlow.value.id)
    }

    fun clearLocalTable(){
        viewModelScope.launch {
            try {
                _dataState.value = PostFeedModelState(loading = true)
                repository.clearLocalTable()
                _edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
            }
        }
    }
}