package com.yurua.trainhard.di

import android.app.Application
import androidx.room.Room
import com.yurua.trainhard.data.WorkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideDatabase(
    app: Application,
    callback: WorkDatabase.Callback
  ) = Room.databaseBuilder(app, WorkDatabase::class.java, "workout_database")
    .fallbackToDestructiveMigration()
    .addCallback(callback)
    .build()

  @Provides
  fun provideWorkoutDao(db: WorkDatabase) = db.workDao()

  @ApplicationScope
  @Provides
  @Singleton
  fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope