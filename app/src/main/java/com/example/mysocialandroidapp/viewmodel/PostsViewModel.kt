package com.example.mysocialandroidapp.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.model.PhotoModel
import com.example.mysocialandroidapp.model.PostFeedModelState
import com.example.mysocialandroidapp.model.PostsFeedModel
import com.example.mysocialandroidapp.repository.PostRepository
import com.example.mysocialandroidapp.samples.Samples
import com.example.mysocialandroidapp.util.SingleLiveEvent
import com.example.mysocialandroidapp.work.RemovePostWorker
import com.example.mysocialandroidapp.work.SavePostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: PostRepository,
    private val workManager: WorkManager,
    val appAuth: AppAuth,
) : ViewModel() {
//    private val _postsFeed = MutableLiveData<PostsFeedModel>()
//    val postsFeed: LiveData<PostsFeedModel>
//        get() = _postsFeed
//
//    init {
//        loadPosts()
//    }
//
//    private fun loadPosts() {
//        val posts = Samples.getPosts()
//        _postsFeed.value = PostsFeedModel(posts, posts?.isEmpty())
//    }

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    //TODO
                    post.copy()//ownedByMe = post.authorId == myId)
                }
            }
        }

    private val _dataState = MutableLiveData<PostFeedModelState>()
    val dataState: LiveData<PostFeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(emptyPost)
    val edited: LiveData<Post>
        get() = _edited


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostFeedModelState(loading = true)
            repository.getAll()
            repository.updateWasSeen()
            _dataState.value = PostFeedModelState()
        } catch (e: Exception) {
            _dataState.value = PostFeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostFeedModelState(refreshing = true)
            repository.getAll()
            repository.updateWasSeen()
            _dataState.value = PostFeedModelState()
        } catch (e: Exception) {
            _dataState.value = PostFeedModelState(error = true)
        }
    }


    fun edit(post: Post) {
        _edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        _edited.value = edited.value?.copy(content = text, author = appAuth.userFlow.value.name, authorId = appAuth.userFlow.value.id)
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = PostFeedModelState(loading = true)
                repository.likeById(id)
                _edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
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

    fun updateWasSeen() {
        viewModelScope.launch {
            try {
                _dataState.value = PostFeedModelState(loading = true)
                repository.updateWasSeen()
                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
            }
        }
    }

    fun clearLocalTable(){
        viewModelScope.launch {
            try {
                _dataState.value = PostFeedModelState(loading = true)
                repository.clearLocalTable()
                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
            }
        }
    }
}