package com.rtuitlab.studo.ui.general.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.rtuitlab.studo.DateRangeValidator
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentCreateEditAdBinding
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.ui.general.MainActivity
import com.rtuitlab.studo.viewmodels.CreateEditAdViewModel
import kotlinx.android.synthetic.main.fragment_create_edit_ad.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CreateEditAdFragment: Fragment(), TimeRangePickerDialog.OnTimeRangeSelectedListener {

    val viewModel: CreateEditAdViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCreateEditAdBinding>(
            inflater,
            R.layout.fragment_create_edit_ad,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collapsingToolbar.title = getString(R.string.create_ad)
        (requireActivity() as MainActivity).enableNavigateButton(collapsingToolbar.toolbar)

        checkSwitch(timeSwitch.isChecked)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        timeSwitch.setOnCheckedChangeListener { _, isChecked -> checkSwitch(isChecked) }
        dateTV.setOnClickListener { showDateRangePickerDialog() }
        timeTV.setOnClickListener { showTimeRangePickerDialog() }
    }

    private fun setObservers() {
        viewModel.adResource.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {

                }
            }
        })
    }

    private fun checkSwitch(isChecked: Boolean) {
        timeTV.isEnabled = !isChecked
//        viewModel.isTimeEnabled.set(!isChecked)
        if (isChecked) {
            timeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_disabled, 0, 0, 0)
        } else {
            timeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, 0, 0)
        }
    }

    private fun showDateRangePickerDialog() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        val currentDate = Calendar.getInstance()
        val tomorrowDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val afterTomorrowDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 2)
        }

        builder.setSelection(androidx.core.util.Pair(tomorrowDate.timeInMillis, afterTomorrowDate.timeInMillis))
        builder.setTitleText(R.string.select_date_range)

        val calendarConstraints = CalendarConstraints.Builder()
        calendarConstraints.setStart(currentDate.timeInMillis)
        calendarConstraints.setValidator(DateRangeValidator(currentDate.timeInMillis))

        builder.setCalendarConstraints(calendarConstraints.build())

        val picker = builder.build()
        picker.show(childFragmentManager, null)

        picker.addOnPositiveButtonClickListener { viewModel.setDateRange(it) }
    }

    private fun showTimeRangePickerDialog() {
//        val dialog = TimeRangePickerDialog.newInstance(this, true)
//        dialog.show(childFragmentManager, null)
    }


    override fun onTimeRangeSelected(startHour: Int, startMin: Int, endHour: Int, endMin: Int) {

    }
}