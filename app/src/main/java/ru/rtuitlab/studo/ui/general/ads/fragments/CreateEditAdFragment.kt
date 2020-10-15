package ru.rtuitlab.studo.ui.general.ads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialContainerTransform
import com.yydcdut.markdown.MarkdownProcessor
import kotlinx.android.synthetic.main.fragment_create_edit_ad.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentCreateEditAdBinding
import ru.rtuitlab.studo.extensions.hideProgress
import ru.rtuitlab.studo.extensions.mainActivity
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.extensions.showProgress
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.ads.models.Ad
import ru.rtuitlab.studo.ui.general.ads.dialogs.TimeRangeDialog
import ru.rtuitlab.studo.utils.DateRangeValidator
import ru.rtuitlab.studo.viewmodels.ads.CreateAd
import ru.rtuitlab.studo.viewmodels.ads.CreateEditAdViewModel
import ru.rtuitlab.studo.viewmodels.ads.EditAd
import ru.rtuitlab.studo.viewmodels.ads.ModifyAdType
import java.util.*

class CreateEditAdFragment: Fragment() {

    companion object {
        const val MODIFY_AD_TYPE_KEY = "MODIFY_AD_TYPE"
    }

    private val viewModel: CreateEditAdViewModel by viewModel()

    private val markdownProcessor: MarkdownProcessor = getKoin().get(named("edit"))

    private val modifyAdType by lazy {
        (arguments?.getSerializable(MODIFY_AD_TYPE_KEY) as? ModifyAdType) ?: CreateAd
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

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
        bindProgressButton(binding.doneBtn)
        binding.doneBtn.attachTextChangeAnimator()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configScreenDependsType()
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
        markdownProcessor.live(descEdit)
        checkSwitch(timeSwitch.isChecked)
        checkDateAndTimeSet()
        setListeners()
        setObservers()
    }

    private fun configScreenDependsType() {
        when(modifyAdType) {
            CreateAd -> {
                collapsingToolbar.title = getString(R.string.create_ad)
                doneBtn.setOnClickListener { viewModel.createAd() }
            }
            is EditAd -> {
                collapsingToolbar.title = getString(R.string.edit_ad)
                doneBtn.text = getString(R.string.save)
                doneBtn.setOnClickListener { viewModel.editAd() }
                viewModel.fillAdData((modifyAdType as EditAd).ad)
                if (!viewModel.isTimeEnabled) {
                    timeSwitch.isChecked = true
                }
            }
        }
    }

    private fun setListeners() {
        timeSwitch.setOnCheckedChangeListener { _, isChecked -> checkSwitch(isChecked) }
        dateTV.setOnClickListener { showDateRangePickerDialog() }
        timeTV.setOnClickListener { showTimeRangePickerDialog() }
    }

    private fun setObservers() {
        viewModel.adResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    requireActivity().onBackPressed()
                    when(modifyAdType) {
                        CreateAd -> {
                            mainActivity().updateStatuses.isNeedToUpdateAdsList = true
                            navigateToAd(it.data!!)
                        }
                        is EditAd -> {
                            mainActivity().updateStatuses.isNeedToUpdateAd = true
                            mainActivity().updateStatuses.isNeedToUpdateAdsList = true
                        }
                    }
                }
                Status.ERROR -> {
                    when(modifyAdType) {
                        CreateAd -> doneBtn.hideProgress(R.string.create)
                        is EditAd -> doneBtn.hideProgress(R.string.save)
                    }
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> {
                    doneBtn.showProgress()
                }
            }
        })
    }

    private fun checkDateAndTimeSet() {
        if (!viewModel.isDateSet) {
            viewModel.dateText.set(getString(R.string.not_selected))
        }
        if (!viewModel.isTimeSet) {
            viewModel.timeText.set(getString(R.string.not_selected))
        }
    }

    private fun checkSwitch(isChecked: Boolean) {
        timeTV.isEnabled = !isChecked
        viewModel.isTimeEnabled = !isChecked
        if (isChecked) {
            timeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_disabled, 0, 0, 0)
        } else {
            timeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, 0, 0)
        }
        viewModel.checkData()
    }

    private fun showDateRangePickerDialog() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        val currentDate = Calendar.getInstance()

        val dateRange = viewModel.getDateRange()
        builder.setSelection(androidx.core.util.Pair(dateRange.first, dateRange.second))
        builder.setTitleText(R.string.select_date_range)

        val calendarConstraints = CalendarConstraints.Builder()
        calendarConstraints.setStart(currentDate.timeInMillis)
        calendarConstraints.setValidator(
            DateRangeValidator(
                currentDate.timeInMillis
            )
        )

        builder.setCalendarConstraints(calendarConstraints.build())

        val picker = builder.build()
        picker.show(childFragmentManager, null)

        picker.addOnPositiveButtonClickListener { viewModel.setDateRange(it) }
    }

    private fun showTimeRangePickerDialog() {
        val timeRange = viewModel.getTimeRange()
        val dialog = TimeRangeDialog.getInstance(
            timeRange.first.first,
            timeRange.first.second,
            timeRange.second.first,
            timeRange.second.second
        )
        dialog.onTimeSetListener = { beginHour, beginMinute, endHour, endMinute ->
            viewModel.setTimeRange(beginHour, beginMinute, endHour, endMinute)
        }
        dialog.show(childFragmentManager, null)
    }

    private fun navigateToAd(ad: Ad) = findNavController().navigate(
        R.id.action_adsListFragment_to_adFragment,
        bundleOf("ad" to ad)
    )
}