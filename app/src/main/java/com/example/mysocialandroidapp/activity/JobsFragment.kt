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
import com.example.mysocialandroidapp.viewmodel.emptyJob
import com.example.mysocialandroidapp.viewmodel.emptyPost
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

        viewModel.userId = viewModel.appAuth.userFlow.value.id

        val adapter = JobsAdapter(object : OnJobInteractionListener {
            override fun onRemove(job: Job) {
                viewModel.removeById(job.id)
            }
            override fun onEdit(job: Job) {
                viewModel.edit(job)
                findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
            }
        })
        binding.jobsList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { x ->
            adapter.submitList(x.jobs)
        }

        binding.fab.setOnClickListener {
            viewModel.edit(emptyJob)
            findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}