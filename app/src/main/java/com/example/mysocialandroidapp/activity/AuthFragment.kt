package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentAuthBinding
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_authentication)

        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        viewModel.data.observe(viewLifecycleOwner) {
            if (viewModel.authenticated) {
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            } else {
                if (binding.editTextLogin.text.toString().isNotBlank())
                    Toast.makeText(this.context, R.string.errorLoginPassword, Toast.LENGTH_LONG)
                        .show()
            }
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignin.setOnClickListener {
            var login = binding.editTextLogin.text.toString().trimIndent()
            var password = binding.editTextPassword.text.toString()
            if (binding.editTextLogin.text.toString().isNotBlank()) {
                try {
                    viewModel.signIn(login, password)
                } catch (e: Exception) {
                    Toast.makeText(this.context, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                 Toast.makeText(this.context, R.string.enterLoginPassword, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}