package com.yurua.trainhard.ui.stat

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.yurua.trainhard.R
import com.yurua.trainhard.R.array
import com.yurua.trainhard.data.Work
import com.yurua.trainhard.data.WorkDao
import com.yurua.trainhard.util.GymData
import com.yurua.trainhard.util.workToGym
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MAX_WORKS_VIEW = 10

@HiltViewModel
class StatViewModel @Inject constructor(
    private val workDao: WorkDao,
    @ApplicationContext ctx: Context
) : ViewModel() {

    val context = ctx

    fun getIndexOfGym(gd: GymData): Int {
        val chest = context.resources.getStringArray(R.array.chest)
        val biceps = context.resources.getStringArray(R.array.biceps)
        val back = context.resources.getStringArray(R.array.back)
        val shoul = context.resources.getStringArray(array.shoulders)
        val legs = context.resources.getStringArray(R.array.legs)
        val triceps = context.resources.getStringArray(R.array.triceps)

        return when (gd.group) {
            "Грудь" -> chest.lastIndexOf(gd.name)
            "Бицепс" -> biceps.lastIndexOf(gd.name)
            "Трицепс" -> triceps.lastIndexOf(gd.name)
            "Спина" -> back.lastIndexOf(gd.name)
            "Плечи" -> shoul.lastIndexOf(gd.name)
            "Ноги" -> legs.lastIndexOf(gd.name)
            else -> -1
        }
    }

    fun getGymsByGroup(str: String): Array<String> {

        val chest = context.resources.getStringArray(array.chest)
        val biceps = context.resources.getStringArray(array.biceps)
        val back = context.resources.getStringArray(array.back)
        val shoul = context.resources.getStringArray(array.shoulders)
        val legs = context.resources.getStringArray(array.legs)
        val triceps = context.resources.getStringArray(array.triceps)

        return when (str) {
            "Грудь" -> chest
            "Бицепс" -> biceps
            "Спина" -> back
            "Плечи" -> shoul
            "Трицепс" -> triceps
            "Ноги" -> legs
            else -> chest
        }
    }

    private val options = arrayListOf<Work>()

    private val statEventChannel = Channel<StatEvent>()
    val statEvent = statEventChannel.receiveAsFlow()

    sealed class StatEvent {
        data class Something(
            val graph1: ArrayList<Entry>,
            val graph2: ArrayList<Entry>,
            val graph3: ArrayList<Entry>
        ) : StatEvent()
    }

    fun getMaxGym(s1: String, s2: String, s3: String): Int {

        val v1 = s1.substring(0, s1.lastIndexOf('x')).toInt()
        val v2 = s2.substring(0, s2.lastIndexOf('x')).toInt()
        val v3 = s3.substring(0, s3.lastIndexOf('x')).toInt()

        return maxOf(v1, v2, v3)
    }

    fun queryGraph(str: String) {
        var currPoint = 0

        val entries1 = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        val entries3 = ArrayList<Entry>()
        options.clear()
        // Поиск тренировок выбранной группы мышц
        val data = workDao.getWorkouts(str)
        viewModelScope.launch {
            data.collect {
                // Текущая тренировка для отображения
                var currWork = 0
                for (work in it) {
                    // Если превышено количество тренировок, то выйти из цикла
                    if (currWork == MAX_WORKS_VIEW)
                        break
                    // Список упражнений выбранной тренировки
                    val gymData = workToGym(work)
                    for (gym in gymData) {
                        // Пропускать упражнения других групп
                        if (gym.group != str)
                            continue
                        // Индекс упражнения в списке текущей группы
                        val i = getIndexOfGym(gym)
                        // Максимальный вес из 3-х подходов
                        val max = getMaxGym(gym.set1, gym.set2, gym.set3)
                        // Точка на графике
                        val entry = Entry(currPoint.toFloat(), max.toFloat())

                        // Индекс упражнения 0 - entries1, 1 - entries2, 2 - entries3
                        when (i) {
                            0 -> entries1.add(entry)
                            1 -> entries2.add(entry)
                            2 -> entries3.add(entry)
                        }
                    }
                    currPoint++
                    currWork++
                }
                statEventChannel.send(StatEvent.Something(entries1, entries2, entries3))
            }
        }
    }
}



