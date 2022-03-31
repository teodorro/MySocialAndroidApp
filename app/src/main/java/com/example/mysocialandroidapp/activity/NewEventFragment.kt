package com.example.mysocialandroidapp.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentNewEventBinding
import com.example.mysocialandroidapp.databinding.FragmentNewPostBinding
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.util.StringArg
import com.example.mysocialandroidapp.viewmodel.EventsViewModel
import com.example.mysocialandroidapp.viewmodel.WallViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewEventFragment : Fragment() {
    private val photoRequestCode = 1
    private val cameraRequestCode = 2

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private var _binding: FragmentNewEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewEventBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_new_event)

        arguments?.textArg
            ?.let(binding.content::setText)

        binding.content.setText(viewModel.edited.value?.content)
        binding.date.setText(viewModel.edited.value?.datetime.toString())
        binding.position.setText(viewModel.edited.value?.coords?.toString())
        //binding.link.setText(viewModel.edited.value?.link)

        binding.content.requestFocus()

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(arrayOf(
                    "image/png",
                    "image/jpeg",
                ))
                .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

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
                    viewModel.changeContent(
                        it.content.text.toString(),
                        it.position.text.toString(),
                        it.date.text.toString(),
                        it.link.text.toString(),
                    )

                    viewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editSpeakers.setOnClickListener {
            var userIds = setOf<Long>()
            val listTypeBundle = bundleOf(USER_IDS to userIds)
            findNavController().navigate(R.id.action_newEventFragment_to_speakersFragment, listTypeBundle)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}