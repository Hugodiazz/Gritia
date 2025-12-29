package com.devdiaz.gritia.di

import android.content.Context
import androidx.room.Room
import com.devdiaz.gritia.model.dao.ExerciseDao
import com.devdiaz.gritia.model.dao.MeasurementDao
import com.devdiaz.gritia.model.dao.RoutineDao
import com.devdiaz.gritia.model.dao.UserDao
import com.devdiaz.gritia.model.dao.WorkoutDao
import com.devdiaz.gritia.model.database.GritiaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGritiaDatabase(@ApplicationContext context: Context): GritiaDatabase {
        return Room.databaseBuilder(context, GritiaDatabase::class.java, "gritia_database").build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: GritiaDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideMeasurementDao(database: GritiaDatabase): MeasurementDao {
        return database.measurementDao()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(database: GritiaDatabase): RoutineDao {
        return database.routineDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(database: GritiaDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    @Singleton
    fun provideExerciseDao(database: GritiaDatabase): ExerciseDao {
        return database.exerciseDao()
    }
}
