package com.yurua.trainhard.ui.works

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.data.WorkDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorksViewModel @Inject constructor(
  private val workDao: WorkDao
) : ViewModel() {

  val searchQuery = MutableStateFlow("")
  private val worksFlow = searchQuery.flatMapLatest {
    workDao.getWorkouts(it)
  }
  val works = worksFlow.asLiveData()

  private val workEventChannel = Channel<WorksEvent>()
  val worksEvent = workEventChannel.receiveAsFlow()

  fun onWorkSelected(work: Work) = viewModelScope.launch {
    workEventChannel.send(WorksEvent.NavigateViewGym(work))
  }

  fun onWorkoutSwiped(work: Work) = viewModelScope.launch {
    workDao.delete(work)
    workEventChannel.send(WorksEvent.ShowUndoDeleteWorkoutMessage(work))
  }

  fun onUndoDeleteClick(work: Work) = viewModelScope.launch {
    workDao.insert(work)
  }

  fun addGymHandler() = viewModelScope.launch {
    workEventChannel.send(WorksEvent.NavigateAddGym)
  }

  sealed class WorksEvent {
    object NavigateAddGym : WorksEvent()
    data class NavigateViewGym(val work: Work) : WorksEvent()
    data class ShowUndoDeleteWorkoutMessage(val work: Work) : WorksEvent()
  }

}