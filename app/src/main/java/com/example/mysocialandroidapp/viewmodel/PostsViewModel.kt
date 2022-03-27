package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.model.PostsFeedModel
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostsViewModel @Inject constructor(
    val appAuth: AppAuth,
) : ViewModel() {
    private val _postsFeed = MutableLiveData<PostsFeedModel>()
    val postsFeed: LiveData<PostsFeedModel>
        get() = _postsFeed

    init {
        loadPosts()
    }

    private fun loadPosts() {
        val posts = Samples.getPosts()
        _postsFeed.value = PostsFeedModel(posts, posts?.isEmpty())
    }
}