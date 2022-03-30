package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.WorkManager
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.model.PostFeedModelState
import com.example.mysocialandroidapp.model.PostsFeedModel
import com.example.mysocialandroidapp.repository.UsersRepository
import com.example.mysocialandroidapp.repository.WallRepository
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnotherUserWallViewModel @Inject constructor(
    private val repository: WallRepository,
    private val usersRepository: UsersRepository,
    private val workManager: WorkManager,
    val appAuth: AppAuth,
) : ViewModel() {
    var userId: Long = 0
        set(value) {
            field = value
            viewModelScope.launch {
                usersRepository.data.collect{ x ->
                    var user = x.first { y -> y.id == userId }
                    _username.value = user.name
                    _avatar.value = user.avatar
                }
            }
            viewModelScope.launch {
                repository.clearLocalTable()
            }
            repository.userId = value
            viewModelScope.launch {
                repository.getAllPosts(userId)
            }
        }

    private val _username = MutableLiveData("")
    val username: LiveData<String>
        get() = _username

    private val _avatar = MutableLiveData<Any?>(null)
    val avatar: LiveData<Any?>
        get() = _avatar

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    private val _dataState = MutableLiveData<PostFeedModelState>()
    val dataState: LiveData<PostFeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(emptyPost)
    val edited: LiveData<Post>
        get() = _edited

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    //TODO
                    post.copy()
                }
            }
        }

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

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = PostFeedModelState(loading = true)
                repository.likeById(userId, id)
                _edited.value = edited.value?.copy(likedByMe = true)
                _dataState.value = PostFeedModelState()
            } catch (e: Exception) {
                _dataState.value = PostFeedModelState(error = true)
            }
        }
    }


//    private val _postsFeed = MutableLiveData<PostsFeedModel>()
//    val postsFeed: LiveData<PostsFeedModel>
//        get() = _postsFeed
//
//    var userId: Long = 0
//
//    init {
//        appAuth.authStateFlow.value.let{
//            loadPosts(it.id)
//        }
//    }
//
//    fun loadPosts(userId: Long) {
//        val posts = Samples.getWall(userId)
//        _postsFeed.value = PostsFeedModel(posts, posts?.isEmpty())
//    }
}