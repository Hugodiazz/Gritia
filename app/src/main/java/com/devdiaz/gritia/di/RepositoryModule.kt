package com.devdiaz.gritia.di

import com.devdiaz.gritia.data.repository.BodyMetricsRepository
import com.devdiaz.gritia.data.repository.BodyMetricsRepositoryImpl
import com.devdiaz.gritia.data.repository.RoutineRepository
import com.devdiaz.gritia.data.repository.RoutineRepositoryImpl
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.data.repository.UserRepositoryImpl
import com.devdiaz.gritia.data.repository.WorkoutRepository
import com.devdiaz.gritia.data.repository.WorkoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

        @Binds
        @Singleton
        abstract fun bindBodyMetricsRepository(
                bodyMetricsRepositoryImpl: BodyMetricsRepositoryImpl
        ): BodyMetricsRepository

        @Binds
        @Singleton
        abstract fun bindRoutineRepository(
                routineRepositoryImpl: RoutineRepositoryImpl
        ): RoutineRepository

        @Binds
        @Singleton
        abstract fun bindWorkoutRepository(
                workoutRepositoryImpl: WorkoutRepositoryImpl
        ): WorkoutRepository

        @Binds
        @Singleton
        abstract fun bindAuthRepository(
                authRepositoryImpl: com.devdiaz.gritia.data.repository.AuthRepositoryImpl
        ): com.devdiaz.gritia.data.repository.AuthRepository
}
