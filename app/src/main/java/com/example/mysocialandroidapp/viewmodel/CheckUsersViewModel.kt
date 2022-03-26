package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysocialandroidapp.model.UsersFeedModel
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckUsersViewModel @Inject constructor()
    : ViewModel(){
    private val _selectedUsersFeed = MutableLiveData<UsersFeedModel>()
    val selectedUsersFeed: LiveData<UsersFeedModel>
        get() = _selectedUsersFeed

    fun setSelectedUsers(userIds: Set<Long>){
        val userList = Samples.getUsers().filter { x -> userIds.contains(x.id) }
        _selectedUsersFeed.value = UsersFeedModel(userList, userList.isEmpty())
    }

    private val _allUsersFeed = MutableLiveData<UsersFeedModel>()
    val allUsersFeed: LiveData<UsersFeedModel>
        get() = _allUsersFeed

    fun initAllUsers(){
        _allUsersFeed.value = UsersFeedModel(Samples.getUsers(), false)
    }
}