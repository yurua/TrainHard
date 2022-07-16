package com.yurua.trainhard.ui.groups

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yurua.trainhard.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(app: Application) : AndroidViewModel(app) {

  fun getData(): MutableMap<String, List<String>> {
    val listDetail = mutableMapOf<String, List<String>>()

    val context = getApplication<Application>().applicationContext

    val listCaptions = ArrayList(context.resources.getStringArray(R.array.groups).asList())
    val listIds = listOf(
      R.array.chest,
      R.array.biceps,
      R.array.triceps,
      R.array.legs,
      R.array.shoulders,
      R.array.back
    )

    for (index in listCaptions.indices) {
      val key = listCaptions[index]
      val resId = listIds[index]
      listDetail[key] = ArrayList(context.resources.getStringArray(resId).asList())
    }

    return listDetail
  }

}
