package com.yurua.trainhard.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "work_table")
@Parcelize
data class Work(
  val date: String,
  val group: String,
  val chest: String,
  val biceps: String,
  val triceps: String,
  val legs: String,
  val shoul: String,
  val back: String,
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0
): Parcelable