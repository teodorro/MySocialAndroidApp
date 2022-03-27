package com.example.mysocialandroidapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.adapter.JobsAdapter
import com.example.mysocialandroidapp.adapter.OnJobInteractionListener
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.databinding.FragmentJobsBinding
import com.example.mysocialandroidapp.dto.Job
import com.example.mysocialandroidapp.viewmodel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobsFragment : Fragment()  {
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_jobs)
        _binding = FragmentJobsBinding.inflate(inflater, container, false)

        viewModel.userId = viewModel.appAuth.authStateFlow.value!!.id
        viewModel.loadJobs(viewModel.userId)

        val adapter = JobsAdapter(object : OnJobInteractionListener {
            override fun onRemove(job: Job) {
            }
            override fun onEdit(job: Job) {
                findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
            }
        },
            viewModel.userId)
        binding.jobsList.adapter = adapter

        viewModel.jobsFeed.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.jobs)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}