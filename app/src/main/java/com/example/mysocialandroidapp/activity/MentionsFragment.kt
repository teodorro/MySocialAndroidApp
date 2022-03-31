package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.CheckUsersAdapter
import com.example.mysocialandroidapp.adapter.OnUserCheckListener
import com.example.mysocialandroidapp.databinding.FragmentCheckUsersBinding
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.viewmodel.WallViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MentionsFragment : Fragment(), OnUserCheckListener {
    private var _binding: FragmentCheckUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WallViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private var mentionedIds: MutableSet<Long> = mutableSetOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_mentions)
        _binding = FragmentCheckUsersBinding.inflate(inflater, container, false)

        val adapter = CheckUsersAdapter(this)
        binding.mentionsList.adapter = adapter

        mentionedIds = viewModel.edited.value!!.mentionIds.toMutableSet()
        viewModel.allUsers.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.users)
        }

        return binding.root
    }

    override fun onCheckUser(user: User) {
//        viewModel.edited.value?.mentionIds?.let {
//            if (it.contains(user.id))
//                it.remove(user.id)
//            else
//                it.add(user.id)
//        }
        mentionedIds.let {
            if (it.contains(user.id))
                it.remove(user.id)
            else
                it.add(user.id)
        }
    }

    override fun isCheckboxVisible(user: User): Boolean {
        return true
    }

    override fun isCheckboxChecked(user: User): Boolean {
//        return viewModel.edited.value?.mentionIds?.any { id -> id == user.id } ?: false
        return mentionedIds.any { id -> id == user.id } ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.save -> {
                binding.let{
                    mentionedIds.forEach { id ->
                        viewModel.edited.value?.mentionIds?.let {
                            if (it.contains(id))
                                it.remove(id)
                            else
                                it.add(id)
                        }
                    }
                    findNavController().navigateUp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}