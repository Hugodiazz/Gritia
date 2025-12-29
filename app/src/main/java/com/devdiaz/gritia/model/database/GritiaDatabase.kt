package com.devdiaz.gritia.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devdiaz.gritia.model.dao.ExerciseDao
import com.devdiaz.gritia.model.dao.MeasurementDao
import com.devdiaz.gritia.model.dao.RoutineDao
import com.devdiaz.gritia.model.dao.UserDao
import com.devdiaz.gritia.model.dao.WorkoutDao
import com.devdiaz.gritia.model.entities.BodyMeasurementEntity
import com.devdiaz.gritia.model.entities.ExerciseEntity
import com.devdiaz.gritia.model.entities.ExercisePerformanceLogEntity
import com.devdiaz.gritia.model.entities.RoutineEntity
import com.devdiaz.gritia.model.entities.RoutineExerciseEntity
import com.devdiaz.gritia.model.entities.UserEntity
import com.devdiaz.gritia.model.entities.UserMetricEntity
import com.devdiaz.gritia.model.entities.WorkoutLogEntity

@Database(
        entities =
                [
                        UserEntity::class,
                        BodyMeasurementEntity::class,
                        UserMetricEntity::class,
                        ExerciseEntity::class,
                        RoutineEntity::class,
                        RoutineExerciseEntity::class,
                        WorkoutLogEntity::class,
                        ExercisePerformanceLogEntity::class],
        version = 1,
        exportSchema = false
)
abstract class GritiaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun routineDao(): RoutineDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
}
