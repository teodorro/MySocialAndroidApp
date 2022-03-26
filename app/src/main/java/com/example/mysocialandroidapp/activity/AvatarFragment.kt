package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentAvatarBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvatarFragment : Fragment() {
    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_avatar)

        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
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