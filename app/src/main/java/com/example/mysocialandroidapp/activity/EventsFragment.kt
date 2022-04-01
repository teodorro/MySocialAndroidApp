package com.example.mysocialandroidapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.EventsAdapter
import com.example.mysocialandroidapp.adapter.OnEventInteractionListener
import com.example.mysocialandroidapp.adapter.OnPostInteractionListener
import com.example.mysocialandroidapp.adapter.PostsAdapter
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.databinding.FragmentEventsBinding
import com.example.mysocialandroidapp.databinding.FragmentWallBinding
import com.example.mysocialandroidapp.dto.Event
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.viewmodel.EventsViewModel
import com.example.mysocialandroidapp.viewmodel.WallViewModel
import com.example.mysocialandroidapp.viewmodel.emptyEvent
import com.example.mysocialandroidapp.viewmodel.emptyPost
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList


@AndroidEntryPoint
class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsViewModel by hiltNavGraphViewModels(R.id.nav_graph)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_events)
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        viewModel.clearLocalTable()
        val userId = viewModel.appAuth.authStateFlow.value.id

        val adapter = EventsAdapter(object : OnEventInteractionListener {
            override fun onRemove(event: Event) {
                viewModel.removeById(event.id)
            }

            override fun onEdit(event: Event) {
                viewModel.edit(event)
                findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
            }

            override fun onLike(event: Event) {
                viewModel.likeById(event)
            }

            override fun onParticipate(event: Event) {
                viewModel.participateById(event)
            }

            override fun onShowUsers(event: Event, userListType: UserListType) {
                val userIds = when (userListType) {
                    UserListType.LIKES -> event.likeOwnerIds
                    UserListType.SPEAKERS -> event.speakerIds
                    UserListType.PARTICIPANTS -> event.participantsIds
                    else -> emptySet()
                }
                val listTypeBundle = bundleOf(
                    USER_LIST_TYPE to userListType,
                    USER_IDS to userIds,
                    POST_ID to event.id
                )
                findNavController().navigate(
                    R.id.action_eventsFragment_to_usersFragment,
                    listTypeBundle
                )
            }
        }, userId)
        binding.eventsList.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

//        binding.swiperefresh.setOnRefreshListener {
//            viewModel.refreshPosts()
//            adapter.refresh()
//        }

        viewModel.data.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.events)
        }

        binding.fab.setOnClickListener {
            viewModel.edit(emptyEvent)
            findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
        }
//        adapter.refresh()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}