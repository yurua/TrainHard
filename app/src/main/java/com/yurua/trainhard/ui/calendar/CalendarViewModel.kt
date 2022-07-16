package com.yurua.trainhard.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applandeo.materialcalendarview.EventDay
import com.yurua.trainhard.R.drawable
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.data.WorkDao
import com.yurua.trainhard.util.dateToNumbers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
  val workDao: WorkDao
) : ViewModel() {

  private val eventsDays = arrayListOf<EventDay>()

  class CustomEventDay(day: Calendar, imageResource: Int, val work: Work) :
    EventDay(day, imageResource)

  private val calEventChannel = Channel<CalendarEvents>()
  val calEvent = calEventChannel.receiveAsFlow()

  fun filterEvents(filter: String) {
    eventsDays.clear()
    var cal: Calendar
    val data = workDao.getWorkouts(filter)
    viewModelScope.launch {
      data.collect {
        for (work in it) {
          val(year, month, day) = dateToNumbers(work.date)
          cal = Calendar.getInstance()
          cal.set(year, month, day)
          val eventDay = CustomEventDay(cal, drawable.dots, work)
          eventsDays.add(eventDay)
        }
        calEventChannel.send(CalendarEvents.FilterWorks(eventsDays))
      }
    }
  }

  fun onEventSelected(work: Work) = viewModelScope.launch {
    calEventChannel.send(CalendarEvents.ViewEvent(work))
  }

  sealed class CalendarEvents {
    data class FilterWorks(val data: ArrayList<EventDay>) : CalendarEvents()
    data class ViewEvent(val work: Work) : CalendarEvents()
  }

}