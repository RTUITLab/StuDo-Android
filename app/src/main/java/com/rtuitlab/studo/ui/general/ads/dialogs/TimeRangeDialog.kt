package com.rtuitlab.studo.ui.general.ads.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rtuitlab.studo.R
import kotlinx.android.synthetic.main.dialog_time_range_picker.*
import kotlinx.android.synthetic.main.view_time_picker.view.timePicker

class TimeRangeDialog: BottomSheetDialogFragment() {

    private val timeTag = "times"
    private val nextTag = "next"
    private val doneTag = "done"

    private var beginHour = 9
    private var beginMinute = 0

    private var endHour = 16
    private var endMinute = 0

    var onTimeSetListener: ((Int, Int, Int, Int) -> Unit)? = null

    companion object {
        fun getInstance(
            beginHour: Int,
            beginMinute: Int,
            endHour: Int,
            endMinute: Int
        ): TimeRangeDialog {
            val dialog = TimeRangeDialog()
            dialog.beginHour = beginHour
            dialog.beginMinute = beginMinute
            dialog.endHour = endHour
            dialog.endMinute = endMinute
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_time_range_picker, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.isUserInputEnabled = false

        initViewPager()
        setListeners()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val timesList = savedInstanceState?.getIntegerArrayList(timeTag)
        timesList?.let {
            beginHour = it[0]
            beginMinute = it[1]
            endHour = it[2]
            endMinute = it[3]
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList(timeTag, arrayListOf(beginHour, beginMinute, endHour, endMinute))
        super.onSaveInstanceState(outState)
    }

    private fun initViewPager() {
        pager.adapter = ViewPagerAdapter()

        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.begin_time)
                else -> getString(R.string.end_time)
            }
        }.attach()
    }

    private fun setListeners() {
        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    checkButton(it)
                }
            }
        })

        doneButton.setOnClickListener {
            when(it.tag) {
                nextTag -> {
                    tabs.getTabAt(1)?.select()
                }
                doneTag -> {
                    onTimeSetListener?.invoke(beginHour, beginMinute, endHour, endMinute)
                    dismiss()
                }
            }
        }
    }

    private fun checkButton(state: Int) {
        when(state) {
            0 -> {
                doneButton.text = getString(R.string.next)
                doneButton.tag = nextTag
                doneButton.isEnabled = true
            }
            1 -> {
                doneButton.text = getString(R.string.done)
                doneButton.tag = doneTag
                doneButton.isEnabled = beginHour < endHour ||
                        (beginHour == endHour && beginMinute < endMinute)
            }
        }
    }



    inner class ViewPagerAdapter: RecyclerView.Adapter<ViewPagerAdapter.TimePickerHolder>() {

        override fun getItemCount() = 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimePickerHolder {
            val holder = TimePickerHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_time_picker, parent, false)
            )
            holder.itemView.timePicker.setIs24HourView(true)
            return holder
        }

        override fun onBindViewHolder(holder: TimePickerHolder, position: Int) = holder.bind(position)

        inner class TimePickerHolder internal constructor(view: View): RecyclerView.ViewHolder(view) {
            private val timePicker = view.timePicker

            fun bind(position: Int) {
                when(position) {
                    0 -> {
                        timePicker.hour = beginHour
                        timePicker.minute = beginMinute
                        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                            beginHour = hourOfDay
                            beginMinute = minute
                        }
                    }
                    1 -> {
                        timePicker.hour = endHour
                        timePicker.minute = endMinute
                        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                            endHour = hourOfDay
                            endMinute = minute
                            doneButton.isEnabled = beginHour < endHour ||
                                    (beginHour == endHour && beginMinute < endMinute)
                        }
                    }
                }
                checkButton(position)
            }
        }
    }
}