package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysocialandroidapp.model.UsersFeedModel
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(

) : ViewModel() {
    private val _usersFeed = MutableLiveData<UsersFeedModel>()
    val usersFeed: LiveData<UsersFeedModel>
        get() = _usersFeed

    fun setUsers(userIds: Set<Long>){
        val userList = Samples.getUsers().filter { x -> userIds.contains(x.id) }
        _usersFeed.value = UsersFeedModel(userList, userList.isEmpty())
    }
}