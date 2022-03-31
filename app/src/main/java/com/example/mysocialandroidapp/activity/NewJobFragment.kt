package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentNewJobBinding
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.viewmodel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private var _binding: FragmentNewJobBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewJobBinding.inflate(inflater, container, false)

        binding.editTextName.setText(viewModel.edited.value?.name)
        binding.editTextPosition.setText(viewModel.edited.value?.position)
        binding.editTextStart.setText(viewModel.edited.value?.start.toString())
        binding.editTextFinish.setText(viewModel.edited.value?.finish?.toString())
        binding.editTextLink.setText(viewModel.edited.value?.link)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                binding.let {
                    viewModel.changeContent(
                        it.editTextName.text.toString(),
                        it.editTextPosition.text.toString(),
                        it.editTextStart.text.toString(),
                        it.editTextFinish.text.toString(),
                        it.editTextLink.text.toString(),)
                    viewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                    findNavController().navigateUp()
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