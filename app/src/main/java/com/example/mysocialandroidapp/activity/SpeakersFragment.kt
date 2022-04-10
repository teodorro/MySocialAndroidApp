package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.CheckUsersAdapter
import com.example.mysocialandroidapp.adapter.OnUserCheckListener
import com.example.mysocialandroidapp.databinding.FragmentSpeakersBinding
import com.example.mysocialandroidapp.dto.User
import com.example.mysocialandroidapp.viewmodel.EventsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpeakersFragment : Fragment(), OnUserCheckListener {
    private var _binding: FragmentSpeakersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private var speakerIds: MutableSet<Long> = mutableSetOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_speakers)
        _binding = FragmentSpeakersBinding.inflate(inflater, container, false)

        val adapter = CheckUsersAdapter(this)
        binding.speakersList.adapter = adapter

        speakerIds = viewModel.edited.value!!.speakerIds.toMutableSet()
        viewModel.allUsers.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.users)
        }

        return binding.root
    }

    override fun onCheckUser(user: User) {
        speakerIds.let {
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
        return speakerIds.any { id -> id == user.id } ?: false
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
                    speakerIds.forEach { id ->
                        viewModel.edited.value?.speakerIds?.let {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}