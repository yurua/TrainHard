package com.yurua.trainhard.ui.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.data.WorkDao
import com.yurua.trainhard.ui.GYM_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GymViewModel @Inject constructor(
  private val workDao: WorkDao
) : ViewModel() {

  var currentBlock = 0

  private val gymEventChannel = Channel<GymEvent>()
  val gymEvent = gymEventChannel.receiveAsFlow()


  fun saveWork(work: Work) = viewModelScope.launch {
    workDao.insert(work)
    gymEventChannel.send(GymEvent.NavigateBack(GYM_RESULT_OK))
  }

  sealed class GymEvent {
    data class NavigateBack(val result: Int) : GymEvent()
  }

}

