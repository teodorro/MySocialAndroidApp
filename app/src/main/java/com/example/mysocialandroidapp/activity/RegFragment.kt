package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentRegBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegFragment : Fragment() {
    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignup.setOnClickListener {
            findNavController().navigate(R.id.action_regFragment_to_postsFragment)
        }

        binding.materialButtonLoadAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_regFragment_to_avatarFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_registration)
        _binding = FragmentRegBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}