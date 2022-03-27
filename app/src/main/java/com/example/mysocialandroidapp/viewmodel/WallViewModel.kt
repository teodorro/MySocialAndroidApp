package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.model.PostsFeedModel
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WallViewModel @Inject constructor(
    val appAuth: AppAuth,
) : ViewModel() {
    private val _postsFeed = MutableLiveData<PostsFeedModel>()
    val postsFeed: LiveData<PostsFeedModel>
        get() = _postsFeed

    var userId: Long = 0

    init {
        loadPosts(appAuth.authStateFlow.value!!.id)
    }

    fun loadPosts(userId: Long) {
        val posts = Samples.getWall(userId)
        _postsFeed.value = PostsFeedModel(posts, posts?.isEmpty())
    }
}