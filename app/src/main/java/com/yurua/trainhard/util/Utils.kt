package com.yurua.trainhard.util

import androidx.fragment.app.Fragment
import com.yurua.trainhard.R
import com.yurua.trainhard.data.Work

val <T> T.exhaustive: T
  get() = this

fun getGroups(work: Work): List<String> {
  val list = listOf("Грудь", "Бицепс", "Трицепс", "Ноги", "Плечи", "Спина")

  return list.filter {
    work.group.contains(it, true)
  }
}

interface TitleListener {
  fun setToolbarTitle(str: String)
}

fun getBackgroundByGroup(group: String): Int = when (group) {
  "Грудь" -> R.drawable.dialog_rounded_green
  "Бицепс" -> R.drawable.dialog_rounded_red
  "Трицепс" -> R.drawable.dialog_rounded_yellow
  "Плечи" -> R.drawable.dialog_rounded_blue
  "Ноги" -> R.drawable.dialog_rounded_orange
  "Спина" -> R.drawable.dialog_rounded_cyan
  else -> 0
}

fun getColorByGroup(group: String): Int = when (group) {
  "Грудь" -> R.color.colorGreen
  "Бицепс" -> R.color.colorRed
  "Трицепс" -> R.color.colorYellow
  "Плечи" -> R.color.colorViolet
  "Ноги" -> R.color.colorOrange
  "Спина" -> R.color.colorCyan
  else -> R.color.colorAccent
}

fun monthToInt(month: String) = when (month) {
  "января" -> 0
  "февраля" -> 1
  "марта" -> 2
  "апреля" -> 3
  "мая" -> 4
  "июня" -> 5
  "июля" -> 6
  "августа" -> 7
  "сентября" -> 8
  "октября" -> 9
  "ноября" -> 10
  "декабря" -> 11
  else -> -1
}

fun dateToNumbers(date: String): Triple<Int, Int, Int> {
  val day = date.substring(0, 2).trim()
  val year = date.substring(date.length - 7, date.length - 3).toInt()
  val monthStr = date.substring(day.length + 1, date.length - 8)
  val month = monthToInt(monthStr)

  return Triple(year, month, day.toInt())
}

data class GymData(
  val name: String,  // Название упражнения
  val set1: String,  // Подход 1
  val set2: String,  // Подход 2
  val set3: String,  // Подход 3
  val group: String  // Группа, которой принадлежит упражнение
)

fun workToGym(work: Work): ArrayList<GymData> {

  val gyms = arrayListOf<GymData>()

  val gr = arrayListOf("Грудь", "Бицепс", "Трицепс", "Спина", "Ноги", "Плечи")
  val vals = arrayListOf(
    work.chest,
    work.biceps,
    work.triceps,
    work.back,
    work.legs,
    work.shoul
  )

  val groups = getGroups(work)
  for (p in groups.indices) {
    for (k in gr.indices) {
      // Совпадение по группе
      if (groups[p] == gr[k]) {
        // Данные конкретной группы
        val grName = vals[k]
        // Определить сколько упражнений в данной группе
        val ind = arrayListOf<Int>()
        for (i in grName.indices)
          if (grName[i] == '/')
            ind.add(i)
        when (ind.size) {
          // 1 упражнение в группе
          0 -> {
            val posName = grName.indexOf(":")
            // Название упражнения
            val name = grName.substring(0, posName)
            val pos1 = grName.indexOf("|")
            // 1-й подход
            val gym1 = grName.substring(posName + 1, pos1)
            val pos2 = grName.indexOf("|", pos1 + 1)
            // 2-й подход
            val gym2 = grName.substring(pos1 + 1, pos2)
            // 3-й подход
            val gym3 = grName.substring(pos2 + 1)
            val gd = GymData(name, gym1, gym2, gym3, groups[p])
            gyms.add(gd)
          }
          // 2 упражнения в группе
          1 -> {
            val z = grName.indexOf("/")
            // 1-е упражнение
            val part1 = grName.substring(0, z)
            // 2-е упражнение
            val part2 = grName.substring(z + 1)
            //..........................................................
            val posName = part1.indexOf(":")
            // Название упражнения
            val name = part1.substring(0, posName)
            val pos1 = part1.indexOf("|")
            // 1-й подход
            val gym1 = part1.substring(posName + 1, pos1)
            val pos2 = part1.indexOf("|", pos1 + 1)
            // 2-й подход
            val gym2 = part1.substring(pos1 + 1, pos2)
            // 3-й подход
            val gym3 = part1.substring(pos2 + 1)
            val gd = GymData(name, gym1, gym2, gym3, groups[p])
            gyms.add(gd)
            //...........................................................
            val xposName = part2.indexOf(":")
            // Название упражнения
            val xname = part2.substring(0, xposName)
            val xpos1 = part2.indexOf("|")
            // 1-й подход
            val xgym1 = part2.substring(xposName + 1, xpos1)
            val xpos2 = part2.indexOf("|", xpos1 + 1)
            // 2-й подход
            val xgym2 = part2.substring(xpos1 + 1, xpos2)
            // 3-й подход
            val xgym3 = part2.substring(xpos2 + 1)
            val xgd = GymData(xname, xgym1, xgym2, xgym3, groups[p])
            gyms.add(xgd)
          }
          // 3 упражнения в группе
          2 -> {
            val r = grName.indexOf("/")
            val q = grName.indexOf("/", r + 1)
            // 1-е упражнение
            val part01 = grName.substring(0, r)
            // 2-е упражнение
            val part02 = grName.substring(r + 1, q)
            // 3-е упражнение
            val part03 = grName.substring(q + 1)
            //..............................................................
            val vposName = part01.indexOf(":")
            // Название упражнения
            val vname = part01.substring(0, vposName)
            val vpos1 = part01.indexOf("|")
            // 1-й подход
            val vgym1 = part01.substring(vposName + 1, vpos1)
            val vpos2 = part01.indexOf("|", vpos1 + 1)
            // 2-й подход
            val vgym2 = part01.substring(vpos1 + 1, vpos2)
            // 3-й подход
            val vgym3 = part01.substring(vpos2 + 1)
            val vgd = GymData(vname, vgym1, vgym2, vgym3, groups[p])
            gyms.add(vgd)
            //.........................................................
            val eposName = part02.indexOf(":")
            // Название упражнения
            val ename = part02.substring(0, eposName)
            val epos1 = part02.indexOf("|")
            // 1-й подход
            val egym1 = part02.substring(eposName + 1, epos1)
            val epos2 = part02.indexOf("|", epos1 + 1)
            // 2-й подход
            val egym2 = part02.substring(epos1 + 1, epos2)
            // 3-й подход
            val egym3 = part02.substring(epos2 + 1)
            val egd = GymData(ename, egym1, egym2, egym3, groups[p])
            gyms.add(egd)
            //.......................................................
            val uposName = part03.indexOf(":")
            // Название упражнения
            val uname = part03.substring(0, uposName)
            val upos1 = part03.indexOf("|")
            // 1-й подход
            val ugym1 = part03.substring(uposName + 1, upos1)
            val upos2 = part03.indexOf("|", upos1 + 1)
            // 2-й подход
            val ugym2 = part03.substring(upos1 + 1, upos2)
            // 3-й подход
            val ugym3 = part03.substring(upos2 + 1)
            val ugd = GymData(uname, ugym1, ugym2, ugym3, groups[p])
            gyms.add(ugd)
          }
        }
      }
    }
  }
  return gyms
}