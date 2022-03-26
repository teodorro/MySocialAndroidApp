package com.example.mysocialandroidapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.OnUserClickListener
import com.example.mysocialandroidapp.adapter.UsersAdapter
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.databinding.FragmentUsersBinding
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

const val USER_LIST_TYPE = "USER_LIST_TYPE"
const val USER_IDS = "USER_IDS"
const val USER_ID = "USER_ID"
const val POST_ID = "POST_ID"

@AndroidEntryPoint
class UsersFragment : Fragment(), OnUserClickListener {
    private var userListType: UserListType? = null

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsersViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userListType = it.get(USER_LIST_TYPE) as UserListType
            viewModel.setUsers(it.get(USER_IDS) as Set<Long>)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_users)

        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        val adapter = UsersAdapter(this)
        binding.usersList.adapter = adapter

        viewModel.usersFeed.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.users)
        }

        return binding.root
    }

    override fun onUserClicked(user: User) {
        val userIdBundle = bundleOf(USER_ID to user.id)
        if (user.id == AppAuth.currentAuthorId) {
            findNavController().navigate(R.id.action_usersFragment_to_wallFragment, userIdBundle)
        } else {
            findNavController().navigate(
                R.id.action_usersFragment_to_anotherUserWallFragment,
                userIdBundle
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}