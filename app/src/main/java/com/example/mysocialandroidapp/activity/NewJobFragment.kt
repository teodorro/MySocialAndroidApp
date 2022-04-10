package com.example.mysocialandroidapp.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentNewJobBinding
import com.example.mysocialandroidapp.util.AndroidUtils
import com.example.mysocialandroidapp.util.DateStringFormatter
import com.example.mysocialandroidapp.viewmodel.JobsViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private var _binding: FragmentNewJobBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewJobBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title =
            if (viewModel.edited.value?.id == 0L) getString(R.string.title_new_job)
            else getString(R.string.title_edit_job)

        binding.editTextName.setText(viewModel.edited.value?.name)
        binding.editTextPosition.setText(viewModel.edited.value?.position)
        binding.editTextLink.setText(viewModel.edited.value?.link)
        binding.dateStart.setText(viewModel.edited.value?.let {
            DateStringFormatter.getDateFromInstance(
                Instant.ofEpochSecond(it.start).toString()
            )
        })
        binding.dateFinish.setText(viewModel.edited.value?.finish.let {
            if (it != null) DateStringFormatter.getDateFromInstance(
                Instant.ofEpochSecond(it).toString()
            )
            else DateStringFormatter.getDateFromInstance(Instant.now().toString())
        })
        setDatePickBinding(forDateStart = true, binding.dateStart)
        setDatePickBinding(forDateStart = false, binding.dateFinish)

        return binding.root
    }

    private fun setDatePickBinding(forDateStart: Boolean, button: MaterialButton) {
        calendar.time = Date.from(
            viewModel.edited.value.let {
                if (it == null) Instant.now()
                else Instant.ofEpochSecond(
                    if (forDateStart) it.start
                    else (it.finish ?: Instant.now().epochSecond)
                )
            })
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                button.text = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                    .format(calendar.time)
            }
        var datePickerDialog = DatePickerDialog(
            requireContext(),
            AlertDialog.THEME_HOLO_LIGHT,
            dateSetListener,
            year, month, day
        )
        button.setOnClickListener {
            datePickerDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                binding.let {
                    val finishDate =
                        DateStringFormatter.getZonedDateTimeFromSimpleString(it.dateStart.text.toString())
                    calendar.time = Date.from(Instant.now())
                    val finish = if (finishDate.year == calendar.get(Calendar.YEAR)
                        && finishDate.monthValue == calendar.get(Calendar.MONTH)
                        && finishDate.dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)
                    ) ""
                    else DateStringFormatter.getEpochFromSimpleDate(it.dateFinish.text.toString())
                    viewModel.changeContent(
                        it.editTextName.text.toString(),
                        it.editTextPosition.text.toString(),
                        DateStringFormatter.getEpochFromSimpleDate(it.dateStart.text.toString()),
                        finish,
                        it.editTextLink.text.toString(),
                    )
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