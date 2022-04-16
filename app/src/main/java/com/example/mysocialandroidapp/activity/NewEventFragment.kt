package com.example.mysocialandroidapp.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentNewEventBinding
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.util.DateStringFormatter
import com.example.mysocialandroidapp.util.StringArg
import com.example.mysocialandroidapp.viewmodel.EventsViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


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

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewEventBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title =
            if (viewModel.edited.value?.id == 0L) getString(R.string.title_new_event)
            else getString(R.string.title_edit_event)

        arguments?.textArg
            ?.let(binding.content::setText)

        binding.content.setText(viewModel.edited.value?.content)
        binding.dateEvent.setText(viewModel.edited.value?.datetime.let {
            if (it != null) DateStringFormatter.getDateFromInstance(it)
            else DateStringFormatter.getDateFromInstance(Instant.now().toString())
        })
        binding.timeEvent.setText(viewModel.edited.value?.datetime.let {
            if (it != null) DateStringFormatter.getTimeFromInstance(it)
            else DateStringFormatter.getTimeFromInstance(Instant.now().toString())
        })
        binding.link.setText(viewModel.edited.value?.link)

        binding.content.requestFocus()

        setDatePickBinding()
        setTimePickBinding()

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

        binding.editSpeakers.setOnClickListener {
            var userIds = setOf<Long>()
            val listTypeBundle = bundleOf(USER_IDS to userIds)
            findNavController().navigate(R.id.action_newEventFragment_to_speakersFragment, listTypeBundle)
        }

        binding.editLocation.setOnClickListener {
            findNavController().navigate(R.id.action_newEventFragment_to_mapEventFragment)
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        return binding.root
    }

    private fun setDatePickBinding() {
        viewModel.edited.value?.datetime.let {
            var date = if (it != null) Date.from(Instant.parse(it))
                else Date.from(Instant.now())
            calendar.time = date
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.dateEvent.text = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                        .format(calendar.time)
                }
            var datePickerDialog = DatePickerDialog(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT,
                dateSetListener,
                year, month, day
            )
            binding.dateEvent.setOnClickListener {
                datePickerDialog.show()
            }
        }
    }

    private fun setTimePickBinding() {
        viewModel.edited.value?.datetime.let {
            var date = if (it != null) Date.from(Instant.parse(it))
            else Date.from(Instant.now())
            calendar.time = date
            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            var minute = calendar.get(Calendar.MINUTE)
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.timeEvent.text = SimpleDateFormat("HH:mm", Locale.US)
                        .format(calendar.time)
                }
            var timePickerDialog = TimePickerDialog(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT,
                timeSetListener,
                hour, minute, true
            )
            binding.timeEvent.setOnClickListener {
                timePickerDialog.show()
            }
        }
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
                        DateStringFormatter.getEpochFromDateTime(
                            "${it.dateEvent.text} ${it.timeEvent.text}:00"),
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}