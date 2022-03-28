package com.example.mysocialandroidapp.util

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.activity.PostsFragment
import com.example.mysocialandroidapp.activity.WallFragment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawerNavigator @Inject constructor(
    @ApplicationContext private val context: Context,
){
    private lateinit var _fragmentFrom: Fragment
    private lateinit var _navController: NavController

    fun navigate(fragmentFrom: Fragment, item: MenuItem, navController: NavController){
        _fragmentFrom = fragmentFrom
        _navController = navController
        when (item.itemId){
            R.id.postsFragment -> {
                navigateToPosts()
            }
            R.id.wallFragment -> {
                navigateToWall()
            }
            R.id.eventsFragment -> {

            }
            R.id.jobsFragment -> {

            }
        }
    }

    private fun navigateToWall() {
        when (_fragmentFrom){
            is PostsFragment -> _navController.navigate(
                R.id.action_postsFragment_to_wallFragment)
        }
    }

    private fun navigateToPosts() {
        when (_fragmentFrom){
            is WallFragment -> _navController.navigate(
                R.id.action_wallFragment_to_postsFragment)
        }
    }
}