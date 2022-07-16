package com.yurua.trainhard.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yurua.trainhard.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Work::class], version = 1)
abstract class WorkDatabase: RoomDatabase() {

  abstract fun workDao(): WorkDao

  class Callback @Inject constructor(
    private val database: Provider<WorkDatabase>,
    @ApplicationScope private val applicationScope: CoroutineScope
  ): RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)

      val dao = database.get().workDao()

      applicationScope.launch {
        dao.insert(Work(DateFormat.getDateInstance(DateFormat.LONG).format(Date()),"Грудь/Бицепс","Жим штанги лежа:40x6|80x4|100x1/Жим гантелей:30x4|20x5|15x4/Разведение гантелей:10x12|14x8|20x6", "Молоток в упоре сидя:40x6|80x4|100x1", "-", "-", "-", "-"))
        dao.insert(Work(DateFormat.getDateInstance(DateFormat.LONG).format(Date()),"Бицепс","-", "Молоток в упоре сидя:40x6|80x4|100x1/Подъем гантелей на бицепс сидя:10x8|16x3|20x2/Подъем штанги на бицепс:20x9|14x5|12x8", "-", "-", "-", "-"))
      }

    }
  }

}