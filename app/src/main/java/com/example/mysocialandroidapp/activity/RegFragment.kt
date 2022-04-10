package com.example.mysocialandroidapp.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentRegBinding
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegFragment : Fragment() {
    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private var avatarUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.data.observe(viewLifecycleOwner) {
            if (viewModel.authenticated) {
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            avatarUri = it.uri
            if (it.uri == null)
                binding.photo.setImageResource(R.drawable.ic_avatar)
            else
                binding.photo.setImageURI(it.uri)
            binding.photo.visibility = View.VISIBLE
        }

        return binding.root
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
        when(item.itemId){
            R.id.save -> {
                var login = binding.editTextLogin.text.toString().trimIndent()
                var password = binding.editTextPassword.text.toString()
                var repeatPassword = binding.editTextRepeatPassword.text.toString()
                var name = binding.editTextName.text.toString()
                try {
                    CoroutineScope(Dispatchers.Default).launch {
                        var isValid =
                            viewModel.validateUserData(login, password, repeatPassword, name)

                        if (isValid.isEmpty()) {
                            viewModel.signUp(login, password, name, avatarUri)
                        } else {
                            Toast.makeText(requireContext(), isValid, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this.context, e.message ?: getString(R.string.network_error), Toast.LENGTH_LONG)
                        .show()
                }
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