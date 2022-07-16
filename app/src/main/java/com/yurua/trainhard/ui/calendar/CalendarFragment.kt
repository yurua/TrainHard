package com.yurua.trainhard.ui.calendar

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.yurua.trainhard.R
import com.yurua.trainhard.databinding.FragmentCalendarBinding
import com.yurua.trainhard.ui.MainActivity
import com.yurua.trainhard.ui.calendar.CalendarViewModel.CalendarEvents.FilterWorks
import com.yurua.trainhard.ui.calendar.CalendarViewModel.CalendarEvents.ViewEvent
import com.yurua.trainhard.ui.calendar.CalendarViewModel.CustomEventDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    private var _binding: FragmentCalendarBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCalendarBinding.bind(view)

        (activity as MainActivity).supportActionBar?.subtitle = ""

        binding.groupFilter.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                if (chip.id == checkedId) {
                    viewModel.filterEvents(chip.text.toString())
                    break
                }
            }
        }

        binding.calendarView.setOnDayClickListener { eventDay ->
            val event = eventDay as? CustomEventDay
            event?.let {
                viewModel.onEventSelected(event.work)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.calEvent.collect { event ->
                when (event) {
                    is FilterWorks -> binding.calendarView.setEvents(event.data)
                    is ViewEvent -> {
                        val action =
                            CalendarFragmentDirections.actionCalendarFragmentToGymFragment(event.work)
                        findNavController().navigate(action)
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


