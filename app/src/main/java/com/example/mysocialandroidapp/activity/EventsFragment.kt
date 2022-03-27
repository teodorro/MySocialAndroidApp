package com.example.mysocialandroidapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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
import dagger.hilt.android.AndroidEntryPoint


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

        viewModel.loadEvents()

        val adapter = EventsAdapter(object : OnEventInteractionListener {
            override fun onRemove(event: Event) {
            }

            override fun onEdit(event: Event) {
                findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
            }

            override fun onLike(event: Event) {
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
        }, viewModel.userId)
        binding.eventsList.adapter = adapter

        viewModel.eventsFeed.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.events)
        }

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