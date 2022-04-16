package com.example.mysocialandroidapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentNewPostBinding
import com.example.mysocialandroidapp.dto.Attachment
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.viewmodel.PostsViewModel
import com.example.mysocialandroidapp.viewmodel.emptyPost
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val photoRequestCode = 1
    private val cameraRequestCode = 2
    private val audioRequestCode = 3
    private val audioTestRequestCode = 4

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title =
            if (viewModel.edited.value?.id == 0L) getString(R.string.title_new_post)
            else getString(R.string.title_edit_post)

        binding.edit.setText(viewModel.edited.value?.content)
        binding.edit.requestFocus()

        binding.removePhoto.setOnClickListener {
            viewModel.changeMedia(null, null)
            binding.attachedFile.text = ""
        }

        binding.attach.setOnClickListener {
            val fragm = this
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_attach)
                setOnMenuItemClickListener{ item ->
                    when (item.itemId) {
                        R.id.gallery -> {
                            ImagePicker.with(fragm)
                                .crop()
                                .compress(2048)
                                .galleryOnly()
                                .galleryMimeTypes(
                                    arrayOf(
                                        "image/png",
                                        "image/jpeg",
                                    )
                                )
                                .start(photoRequestCode)
                            true
                        }
                        R.id.camera -> {
                            ImagePicker.with(fragm)
                                .crop()
                                .compress(2048)
                                .cameraOnly()
                                .start(cameraRequestCode)
                            true
                        }
                        R.id.audio -> {
                            val intent = Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT)
                            startActivityForResult(Intent.createChooser(intent, "Select a file"), audioRequestCode)
                            true
                        }
                        R.id.audioTest -> {
                            viewModel.setTestAudioAttachment()
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding.editLocation.setOnClickListener {
            findNavController().navigate(R.id.action_newPostFragment_to_mapPostFragment)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }
            if (it.type == AttachmentType.IMAGE) {
                binding.photoContainer.visibility = View.VISIBLE
                binding.photo.setImageURI(it.uri)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                binding.let {
                    viewModel.changeContent(it.edit.text.toString())
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
        binding.editMentions.setOnClickListener {
            var userIds = setOf<Long>()
            val listTypeBundle = bundleOf(USER_IDS to userIds)
            findNavController().navigate(
                R.id.action_newPostFragment_to_mentionsFragment,
                listTypeBundle
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            binding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changeMedia(uri, file)
            binding.attachedFile.text = file?.name.toString()
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changeMedia(uri, file)
            binding.attachedFile.text = file?.name.toString()
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == audioRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = File(uri?.path)
            viewModel.changeMedia(uri, file, AttachmentType.AUDIO)
            binding.attachedFile.text = file?.name.toString()
            return
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.edited.value?.let { it ->
                        it.copy(mentionIds = mutableSetOf(), mentionedMe = false)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}