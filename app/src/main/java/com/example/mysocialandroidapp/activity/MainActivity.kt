package com.example.mysocialandroidapp.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.api.TIMEOUT
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.databinding.ActivityMainBinding
import com.example.mysocialandroidapp.databinding.NavHeaderMainBinding
import com.example.mysocialandroidapp.util.DrawerNavigator
import com.example.mysocialandroidapp.util.ImageSetter
import com.example.mysocialandroidapp.viewmodel.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(
) : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var appAuth: AppAuth
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    @Inject
    lateinit var drawerNavigator: DrawerNavigator

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // region setting up drawer and appbar
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.postsFragment, R.id.wallFragment, R.id.eventsFragment, R.id.jobsFragment), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener(this)
        // endregion

        this.supportActionBar?.title = ""

        authViewModel.moveToAuthEvent.observe(this) {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.action_postsFragment_to_authFragment)
        }

        authViewModel.moveToSignUpEvent.observe(this){
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.action_postsFragment_to_regFragment)
        }

        authViewModel.signOutEvent.observe(this) {
            authViewModel.signOut()
            navView.menu.setGroupVisible(R.id.authenticated, false)
            navView.menu.setGroupVisible(R.id.unauthenticated, true)
        }

        authViewModel.authenticatedEvent.observe(this) {
            navView.menu.setGroupVisible(R.id.authenticated, true)
            navView.menu.setGroupVisible(R.id.unauthenticated, false)
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful)
                Log.d(null, "Something wrong happened: ${task.exception}")
            val token = task.result
            Log.d(null, token)
        }

        with(authViewModel){
            if (authenticated) {
                val prefs = getSharedPreferences(
                    "auth",
                    Context.MODE_PRIVATE)
                initUser(prefs.getLong("id", 0))
            }
        }

        var headerView = binding.navView.getHeaderView(0)
        val headerBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        authViewModel.user.observe(this) {
            invalidateOptionsMenu() //??
            navView.menu.setGroupVisible(R.id.authenticated, it.id > 0)
            if (authViewModel.authenticated) {
                with(headerBinding){
                    textViewName.text = it.name
                    textViewLogin.text = it.login
                    if (it.avatar != null) {
                        ImageSetter.set(imageViewAvatar, it.avatar, circleCrop = true)
                    } else {
                        imageViewAvatar.setImageResource(R.mipmap.ic_launcher_round)
                    }
                }
            } else {
                with(headerBinding){
                    textViewName.text = "Not authenticated"
                    textViewLogin.text = ""
                    imageViewAvatar.setImageResource(R.mipmap.ic_launcher_round)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_authentication, menu)
        menu.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
        menu.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                authViewModel.moveToAuthInvoke()
                true
            }
            R.id.signup -> {
                authViewModel.signUpInvoke()
                true
            }
            R.id.signout -> {
                var curFragment = getCurrentFragment()
                if (curFragment != null) {
                    drawerNavigator.navigate(
                        getCurrentFragment()!!,
                        R.id.postsFragment,
                        findNavController(R.id.nav_host_fragment_content_main)
                    )
                }
                authViewModel.signOutInvoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCurrentFragment() : Fragment? {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var curFragment = getCurrentFragment()
        if (curFragment != null) {
            drawerNavigator.navigate(
                getCurrentFragment()!!,
                item.itemId,
                findNavController(R.id.nav_host_fragment_content_main)
            )
            binding.drawerLayout.close()
        }
        else
            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
        return true
    }

}