package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.CheckUsersAdapter
import com.example.mysocialandroidapp.adapter.OnUserCheckListener
import com.example.mysocialandroidapp.databinding.FragmentCheckUsersBinding
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.enumeration.UserListType
import com.example.mysocialandroidapp.viewmodel.CheckUsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckUsersFragment : Fragment(), OnUserCheckListener {
    private var userListType: UserListType? = null

    private var _binding: FragmentCheckUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckUsersViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            viewModel.setSelectedUsers(it.get(USER_IDS) as Set<Long>)
            viewModel.initAllUsers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_mentions)

        _binding = FragmentCheckUsersBinding.inflate(inflater, container, false)

        val adapter = CheckUsersAdapter(this)
        binding.mentionsList.adapter = adapter

        viewModel.allUsersFeed.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.users)
        }

        return binding.root
    }

    override fun onCheckUser(user: User) {
        viewModel.selectedUsersFeed
    }

    override fun isCheckboxVisible(user: User): Boolean {
        return true
    }

    override fun isCheckboxChecked(user: User): Boolean {
        return viewModel.selectedUsersFeed.value?.users?.any { x -> x.id == user.id } ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.save -> {
                binding.let{
                    findNavController().navigateUp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}