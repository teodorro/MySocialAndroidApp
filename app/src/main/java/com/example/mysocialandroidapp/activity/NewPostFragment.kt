package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentNewPostBinding
import com.example.mysocialandroidapp.viewmodel.WallViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WallViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_new_post)

        binding.edit.setText(getString(R.string.hello_blank_fragment))
        binding.edit.requestFocus()

        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editMentions.setOnClickListener {
            var userIds = setOf<Long>()
            val listTypeBundle = bundleOf(USER_IDS to userIds)
            findNavController().navigate(R.id.action_newPostFragment_to_checkUsersFragment, listTypeBundle)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}