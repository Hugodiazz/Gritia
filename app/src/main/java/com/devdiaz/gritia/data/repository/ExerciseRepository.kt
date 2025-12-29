package com.devdiaz.gritia.data.repository

import com.devdiaz.gritia.di.IoDispatcher
import com.devdiaz.gritia.model.Equipment
import com.devdiaz.gritia.model.Exercise
import com.devdiaz.gritia.model.MuscleGroup
import com.devdiaz.gritia.model.dao.ExerciseDao
import com.devdiaz.gritia.model.mappers.toDomain
import com.devdiaz.gritia.model.mappers.toEntity
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    suspend fun seedInitialExercises()
}

class ExerciseRepositoryImpl
@Inject
constructor(
        private val exerciseDao: ExerciseDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
            exerciseDao.getAllExercises().map { entities ->
                entities.map { it.toDomain() }
            }

    override suspend fun seedInitialExercises() =
            withContext(ioDispatcher) {
                val existing = exerciseDao.getAllExercises().first()
                if (existing.isEmpty()) {
                    val initialExercises =
                            listOf(
                                    Exercise(
                                            id = "1", // Will be ignored by autoGenerate if 0, but we want 1? No, 
                                            // let DB handle ID, but we need consistency for now.
                                            // Actually, if we supply ID > 0, Room might accept it if not conflicting.
                                            // Let's use 0 to let DB generate, but then UI mock data IDs won't match 
                                            // unless we re-fetch.
                                            // Solution: UI will now observe DB, so it will get the REAL IDs.
                                            name = "Arnold Press",
                                            muscleGroup = MuscleGroup.SHOULDERS,
                                            equipment = Equipment.DUMBBELL,
                                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDTZTfpIwLPQcBzghnH9jiM_TvFpmfWprFAhUF9li2eAtsjSlU-V0mUXimjQO9O9IBfiyYDNj-dNGZogaDOEdTEhjKrDh0u9_M5pQWciRq2rC9CLLXX0qm159u7156DeSscjg7zzOYEET_U8D9TK2-0mtc3ZKaGm7gTvlt6NNHQlzYUPTCIkXl5ZIWg2vqT9s8WK9pniGzKKLPVI_GqI5et-_kS4rkUkkY8vCdhcgHh5KcWXAIvvtPMgrPgp7jajAVIGq8rXgLzEbM"
                                    ),
                                    Exercise(
                                            id = "2",
                                            name = "Bicep Curl",
                                            muscleGroup = MuscleGroup.ARMS,
                                            equipment = Equipment.DUMBBELL,
                                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDsZJHUzqZLFESuwodpjP89zwcLasHOO8BMsjb3Rp32KqjKMeuOC9rbbd5fkoGFm3WrokLf1ykfUeBbW6pDXVpeBd0-Ih1GeohOrDw450itcUVwq58OwTbnf73wNhOaQ1xHESEOrWfJo9FVE0Sg79phyL_Xyl0QRfUyHXLBd1ObmxPj_isKVElvf817sQMlBCvtfCQiyRfWxXOawJF5HKrCqisxDm5UuHJhmRSCuQBIOvnah6CbedDVaNPYv7p6Y5Yc2mQ_Q6fa-Ck"
                                    ),
                                    Exercise(
                                            id = "3",
                                            name = "Cable Fly",
                                            muscleGroup = MuscleGroup.CHEST,
                                            equipment = Equipment.MACHINE,
                                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCpkASJQns2Y7lceKpWt8xPImpyRh-0TShgEgZxX9yPuOs1T-D7l4EvXoP27NqUbD4POPJR-cUMzaQR_XiTWVkK1jEKuOXzchHtpntyz59bD9L6JfTu7PfL_tF2TElQRzVM5ROQKqS4-NaWSB_KHDgHdpNPr3JsEK54rC1kmEmF4o2NmQmPUnUQpu0oHjQsIpKdRt3sABd1pAv6sJEnBlGcNLCtdN_HkidnvckCJP24n7_s7E5qo6kaT2qahKLLmtpbBw7uZfMKzI8"
                                    ),
                                    Exercise(
                                            id = "4",
                                            name = "Back Squat",
                                            muscleGroup = MuscleGroup.LEGS,
                                            equipment = Equipment.BARBELL,
                                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB-iO-Hbr8vHva3MGszAPSPNczOCTKDcLr2IHvXx1jd4gXZ_ML6BOOvQIvulCORmkMDbzDXymbfFkrzhdnlP_a3COhkdLnkqmAPDr82mKAdiZ_cQJUdpS0Zjw-RgjuQGJFSbuIXhrryw6wQOPLFWfsdNqxTSvvlUv-NpMXUTQjaYsY704FH51veVkudtyJ1Wf4XkGMlH73bd4Lau5lT9ppdLwdkLyxQPGMw58cuL03ERlwcjImIUiY4yk4ZZKoOOrUOlhJ2O26Ntng"
                                    ),
                                    Exercise(
                                            id = "5",
                                            name = "Tricep Pushdown",
                                            muscleGroup = MuscleGroup.ARMS,
                                            equipment = Equipment.MACHINE,
                                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAOJQYw6LLv-oeWVPvGmokhWr510lKbzM0kCHa23L0JM9pH7byFeq7PeaEDGJXLcPjC8vBk7F7n6UnaEaUs7GWDzVlbKqKNoT8n-k0fjgMidxgmzRGInojFisY-gmQCvbx7dXoM_XKMfaw5q-4fErogRvfaUgG5x0UtMGmB4stoLDVtubYhvOv8q9Qixyyhtgdqinb49sZzR8-aouJBxsU2oOH8O0rdeh2W8I3QKse4Wpd-PAap9bWEgz89fKO2ZVrh__DIJJFg2AA"
                                    )
                            )
                    exerciseDao.insertExercises(initialExercises.map { it.toEntity() })
                }
            }
}
