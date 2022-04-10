package com.example.mysocialandroidapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.JobsAdapter
import com.example.mysocialandroidapp.adapter.OnJobInteractionListener
import com.example.mysocialandroidapp.databinding.FragmentAnotherUserJobsBinding
import com.example.mysocialandroidapp.dto.Job
import com.example.mysocialandroidapp.viewmodel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnotherUserJobsFragment : Fragment() {
    private var _binding: FragmentAnotherUserJobsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

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
        _binding = FragmentAnotherUserJobsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_jobs)

        viewModel.loadJobs()

        val adapter = JobsAdapter(object : OnJobInteractionListener {
            override fun onRemove(job: Job) {
            }
            override fun onEdit(job: Job) {
                findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
            }
        }, showPopupMenu = false)
        binding.jobsList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.jobs)
        }

        viewModel.username.observe(viewLifecycleOwner){
            binding.username.text = viewModel.username.value
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTypeBundle = bundleOf( USER_ID to viewModel.userId)
        binding.buttonWall.setOnClickListener {
            findNavController().navigate(R.id.action_anotherUserJobsFragment_to_anotherUserWallFragment, listTypeBundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}