package com.yurua.trainhard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDao {

  @Query("SELECT * FROM work_table WHERE `group` LIKE '%' || :searchQuery || '%' ")
  fun getWorkouts(searchQuery: String): Flow<List<Work>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(work: Work)

  @Update
  suspend fun update(work: Work)

  @Delete
  suspend fun delete(work: Work)

}