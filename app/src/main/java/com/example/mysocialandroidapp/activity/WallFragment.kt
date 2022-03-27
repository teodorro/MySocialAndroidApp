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
import com.example.mysocialandroidapp.adapter.OnPostInteractionListener
import com.example.mysocialandroidapp.adapter.PostsAdapter
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.databinding.FragmentWallBinding
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.viewmodel.WallViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WallFragment : Fragment() {
    private var _binding: FragmentWallBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WallViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_wall)
        _binding = FragmentWallBinding.inflate(inflater, container, false)

        viewModel.userId = viewModel.appAuth.authStateFlow.value!!.id
        viewModel.loadPosts(viewModel.userId)

        val adapter = PostsAdapter(object : OnPostInteractionListener {
            override fun onRemove(post: Post) {
            }

            override fun onEdit(post: Post) {
                findNavController().navigate(R.id.action_wallFragment_to_newPostFragment)
            }

            override fun onLike(post: Post) {
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
                    R.id.action_wallFragment_to_usersFragment,
                    listTypeBundle
                )
            }
        }, viewModel.userId)
        binding.postsList.adapter = adapter

        viewModel.postsFeed.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.posts)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_wallFragment_to_newPostFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}