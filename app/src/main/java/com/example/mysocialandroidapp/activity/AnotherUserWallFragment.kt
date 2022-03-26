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
import com.example.mysocialandroidapp.adapter.OnPostInteractionListener
import com.example.mysocialandroidapp.adapter.PostsAdapter
import com.example.mysocialandroidapp.databinding.FragmentAnotherUserWallBinding
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.samples.Samples
import com.example.mysocialandroidapp.viewmodel.AnotherUserWallViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnotherUserWallFragment : Fragment() {
    private var _binding: FragmentAnotherUserWallBinding? = null
    private val binding get() = _binding!!

    private val viewModelUser: AnotherUserWallViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModelUser.userId = it.get(USER_ID) as Long
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnotherUserWallBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_wall)

        viewModelUser.loadPosts(viewModelUser.userId)

        val adapter = PostsAdapter(object : OnPostInteractionListener {
            override fun onRemove(post: Post) {
            }
            override fun onEdit(post: Post) {
                findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
            }
            override fun onLike(post: Post) {
            }
            override fun onShowUsers(post: Post, userListType: UserListType) {
                val userIds = when (userListType) {
                    UserListType.LIKES -> post.likeOwnerIds
                    else -> emptySet()
                }
                val listTypeBundle = bundleOf(USER_LIST_TYPE to userListType, USER_IDS to userIds, POST_ID to post.id)
                findNavController().navigate(
                    R.id.action_wallFragment_to_usersFragment,
                    listTypeBundle
                )
            }
        }, viewModelUser.userId)
        binding.postsList.adapter = adapter

        binding.username.text = Samples.getUsers().first { x -> x.id == viewModelUser.userId }.name

        viewModelUser.postsFeed.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.posts)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTypeBundle = bundleOf( USER_ID to viewModelUser.userId)
        binding.buttonJobs.setOnClickListener {
            findNavController().navigate(R.id.action_anotherUserWallFragment_to_anotherUserJobsFragment, listTypeBundle)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}