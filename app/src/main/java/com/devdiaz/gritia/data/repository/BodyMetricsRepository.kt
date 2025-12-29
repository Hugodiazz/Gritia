package com.devdiaz.gritia.data.repository

import com.devdiaz.gritia.di.IoDispatcher
import com.devdiaz.gritia.model.dao.MeasurementDao
import com.devdiaz.gritia.model.entities.BodyMeasurementEntity
import com.devdiaz.gritia.model.entities.UserMetricEntity
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface BodyMetricsRepository {
        fun getBodyMeasurements(userId: Long): Flow<List<BodyMeasurementEntity>>
        fun getUserMetrics(userId: Long): Flow<List<UserMetricEntity>>
        suspend fun addMeasurement(measurement: BodyMeasurementEntity)
        suspend fun addMetric(metric: UserMetricEntity)

        suspend fun getMetricOnDate(userId: Long, start: Long, end: Long): UserMetricEntity?
        suspend fun getLatestMetricBeforeDate(userId: Long, timestamp: Long): UserMetricEntity?
        suspend fun getMeasurementOnDate(
                userId: Long,
                start: Long,
                end: Long
        ): BodyMeasurementEntity?
        suspend fun getLatestMeasurementBeforeDate(
                userId: Long,
                timestamp: Long
        ): BodyMeasurementEntity?
        suspend fun deleteUserMetric(id: Long)
        suspend fun deleteBodyMeasurement(id: Long)
}

class BodyMetricsRepositoryImpl
@Inject
constructor(
        private val measurementDao: MeasurementDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BodyMetricsRepository {

        override fun getBodyMeasurements(userId: Long): Flow<List<BodyMeasurementEntity>> =
                measurementDao.getBodyMeasurements(userId)

        override fun getUserMetrics(userId: Long): Flow<List<UserMetricEntity>> =
                measurementDao.getUserMetrics(userId)

        override suspend fun addMeasurement(measurement: BodyMeasurementEntity) =
                withContext(ioDispatcher) {
                        measurementDao.insertMeasurement(measurement)
                        Unit
                }

        override suspend fun addMetric(metric: UserMetricEntity) =
                withContext(ioDispatcher) { measurementDao.insertMetric(metric) }

        override suspend fun getMetricOnDate(
                userId: Long,
                start: Long,
                end: Long
        ): UserMetricEntity? =
                withContext(ioDispatcher) {
                        measurementDao.getUserMetricByDateRange(userId, start, end)
                }

        override suspend fun getLatestMetricBeforeDate(
                userId: Long,
                timestamp: Long
        ): UserMetricEntity? =
                withContext(ioDispatcher) {
                        measurementDao.getLatestUserMetricBeforeDate(userId, timestamp)
                }

        override suspend fun getMeasurementOnDate(
                userId: Long,
                start: Long,
                end: Long
        ): BodyMeasurementEntity? =
                withContext(ioDispatcher) {
                        measurementDao.getBodyMeasurementByDateRange(userId, start, end)
                }

        override suspend fun getLatestMeasurementBeforeDate(
                userId: Long,
                timestamp: Long
        ): BodyMeasurementEntity? =
                withContext(ioDispatcher) {
                        measurementDao.getLatestBodyMeasurementBeforeDate(userId, timestamp)
                }

        override suspend fun deleteUserMetric(id: Long) =
                withContext(ioDispatcher) { measurementDao.deleteMetricById(id) }

        override suspend fun deleteBodyMeasurement(id: Long) =
                withContext(ioDispatcher) { measurementDao.deleteMeasurementById(id) }
}
