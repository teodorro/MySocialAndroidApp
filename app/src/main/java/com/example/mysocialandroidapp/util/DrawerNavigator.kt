package com.example.mysocialandroidapp.util

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.activity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawerNavigator @Inject constructor(
    @ApplicationContext private val context: Context,
){
    private lateinit var _fragmentFrom: Fragment
    private lateinit var _navController: NavController

    fun navigate(fragmentFrom: Fragment, menuItemId: Int, navController: NavController){
        _fragmentFrom = fragmentFrom
        _navController = navController
        when (menuItemId){
            R.id.postsFragment -> {
                navigateToPosts()
            }
            R.id.wallFragment -> {
                navigateToWall()
            }
            R.id.eventsFragment -> {
                navigateToEvents()
            }
            R.id.jobsFragment -> {
                navigateToJobs()
            }
        }
    }

    private fun navigateToEvents() {
        when (_fragmentFrom){
            is WallFragment -> _navController.navigate(
                R.id.action_wallFragment_to_eventsFragment)
            is UsersFragment -> _navController.navigate(
                R.id.action_usersFragment_to_eventsFragment)
            is AnotherUserWallFragment -> _navController.navigate(
                R.id.action_anotherUserWallFragment_to_eventsFragment)
            is NewPostFragment -> _navController.navigate(
                R.id.action_newPostFragment_to_eventsFragment)
            is MentionsFragment -> _navController.navigate(
                R.id.action_mentionsFragment_to_eventsFragment)
            is PostsFragment -> _navController.navigate(
                R.id.action_postsFragment_to_eventsFragment)
            is AnotherUserJobsFragment -> _navController.navigate(
                R.id.action_anotherUserJobsFragment_to_eventsFragment)
            is NewJobFragment -> _navController.navigate(
                R.id.action_newJobFragment_to_eventsFragment)
            is NewEventFragment -> _navController.navigate(
                R.id.action_newEventFragment_to_eventsFragment)
            is JobsFragment -> _navController.navigate(
                R.id.action_jobsFragment_to_eventsFragment)
        }
    }

    private fun navigateToJobs() {
        when (_fragmentFrom){
            is WallFragment -> _navController.navigate(
                R.id.action_wallFragment_to_jobsFragment)
            is UsersFragment -> _navController.navigate(
                R.id.action_usersFragment_to_jobsFragment)
            is AnotherUserWallFragment -> _navController.navigate(
                R.id.action_anotherUserWallFragment_to_jobsFragment)
            is NewPostFragment -> _navController.navigate(
                R.id.action_newPostFragment_to_jobsFragment)
            is MentionsFragment -> _navController.navigate(
                R.id.action_mentionsFragment_to_jobsFragment)
            is PostsFragment -> _navController.navigate(
                R.id.action_postsFragment_to_jobsFragment)
            is AnotherUserJobsFragment -> _navController.navigate(
                R.id.action_anotherUserJobsFragment_to_jobsFragment)
            is NewJobFragment -> _navController.navigate(
                R.id.action_newJobFragment_to_jobsFragment)
            is NewEventFragment -> _navController.navigate(
                R.id.action_newEventFragment_to_jobsFragment)
            is EventsFragment -> _navController.navigate(
                R.id.action_eventsFragment_to_jobsFragment)
        }
    }

    private fun navigateToWall() {
        when (_fragmentFrom){
            is PostsFragment -> _navController.navigate(
                R.id.action_postsFragment_to_wallFragment)
            is UsersFragment -> _navController.navigate(
                R.id.action_usersFragment_to_wallFragment)
            is AnotherUserWallFragment -> _navController.navigate(
                R.id.action_anotherUserWallFragment_to_wallFragment)
            is NewPostFragment -> _navController.navigate(
                R.id.action_newPostFragment_to_wallFragment)
            is MentionsFragment -> _navController.navigate(
                R.id.action_mentionsFragment_to_wallFragment)
            is JobsFragment -> _navController.navigate(
                R.id.action_jobsFragment_to_wallFragment)
            is AnotherUserJobsFragment -> _navController.navigate(
                R.id.action_anotherUserJobsFragment_to_wallFragment)
            is NewJobFragment -> _navController.navigate(
                R.id.action_newJobFragment_to_wallFragment)
            is NewEventFragment -> _navController.navigate(
                R.id.action_newEventFragment_to_wallFragment)
            is EventsFragment -> _navController.navigate(
                R.id.action_eventsFragment_to_wallFragment)
        }
    }

    private fun navigateToPosts() {
        when (_fragmentFrom){
            is WallFragment -> _navController.navigate(
                R.id.action_wallFragment_to_postsFragment)
            is UsersFragment -> _navController.navigate(
                R.id.action_usersFragment_to_postsFragment)
            is AnotherUserWallFragment -> _navController.navigate(
                R.id.action_anotherUserWallFragment_to_postsFragment)
            is NewPostFragment -> _navController.navigate(
                R.id.action_newPostFragment_to_postsFragment)
            is MentionsFragment -> _navController.navigate(
                R.id.action_mentionsFragment_to_postsFragment)
            is JobsFragment -> _navController.navigate(
                R.id.action_jobsFragment_to_postsFragment)
            is AnotherUserJobsFragment -> _navController.navigate(
                R.id.action_anotherUserJobsFragment_to_postsFragment)
            is NewJobFragment -> _navController.navigate(
                R.id.action_newJobFragment_to_postsFragment)
            is NewEventFragment -> _navController.navigate(
                R.id.action_newEventFragment_to_postsFragment)
            is EventsFragment -> _navController.navigate(
                R.id.action_eventsFragment_to_postsFragment)
        }
    }
}