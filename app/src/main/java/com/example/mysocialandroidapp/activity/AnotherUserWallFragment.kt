package com.example.mysocialandroidapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.OnPostInteractionListener
import com.example.mysocialandroidapp.adapter.PostsAdapter
import com.example.mysocialandroidapp.databinding.FragmentAnotherUserWallBinding
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.samples.Samples
import com.example.mysocialandroidapp.viewmodel.AnotherUserWallViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AnotherUserWallFragment : Fragment() {
    private var _binding: FragmentAnotherUserWallBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnotherUserWallViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.userId = it.get(USER_ID) as Long
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnotherUserWallBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_wall)

        var currentUserId = viewModel.appAuth.userFlow.value.id
        viewModel.loadPosts()

        val adapter = PostsAdapter(object : OnPostInteractionListener {
            override fun onRemove(post: Post) {
            }
            override fun onEdit(post: Post) {
            }
            override fun onLike(post: Post) {
                viewModel.likeById(currentUserId, post)
            }
            override fun onShowUsers(post: Post, userListType: UserListType) {
                val userIds = when (userListType) {
                    UserListType.LIKES -> post.likeOwnerIds
                    UserListType.MENTIONS -> post.mentionIds
                    else -> emptySet()
                }
                val listTypeBundle = bundleOf(
                    USER_LIST_TYPE to userListType,
                    USER_IDS to userIds,
                    POST_ID to post.id
                )
                findNavController().navigate(
                    R.id.action_anotherUserWallFragment_to_usersFragment,
                    listTypeBundle
                )
            }
        }, 0) // 0 to hide author menu items
        binding.postsList.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
            adapter.refresh()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        viewModel.username.observe(viewLifecycleOwner){
            binding.username.text = viewModel.username.value
        }

        adapter.refresh()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTypeBundle = bundleOf( USER_ID to viewModel.userId)
        binding.buttonJobs.setOnClickListener {
            findNavController().navigate(R.id.action_anotherUserWallFragment_to_anotherUserJobsFragment, listTypeBundle)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}