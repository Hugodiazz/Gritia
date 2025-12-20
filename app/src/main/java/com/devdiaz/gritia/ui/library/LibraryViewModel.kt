package com.devdiaz.gritia.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.model.Equipment
import com.devdiaz.gritia.model.Exercise
import com.devdiaz.gritia.model.MuscleGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class LibraryUiState(
        val popularExercises: List<Exercise> = emptyList(),
        val filteredExercises: List<Exercise> = emptyList(),
        val selectedMuscleGroup: MuscleGroup = MuscleGroup.ALL,
        val searchQuery: String = ""
)

@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {

    private val _selectedMuscleGroup = MutableStateFlow(MuscleGroup.ALL)
    private val _searchQuery = MutableStateFlow("")

    private val allExercises =
            listOf(
                    Exercise(
                            id = "1",
                            name = "Barbell Bench Press",
                            muscleGroup = MuscleGroup.CHEST,
                            equipment = Equipment.BARBELL,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuAYVbB9oxiwBqAHLTumO8s6iGs-RHlpbYV_N-y2j9sdzTt04ZNBe0GRMlUTDG3Du2UUcZyhHp2DsYjbS3oPkTk3alT3232Ej5WQHpYM4HeeFMUMOxmfAlsRLL2fYBvpJrjnDO8-pK5goYJgXEPvK2rwHogO0dcyUHwBkqLdkP7ZPsUMhXyYpGEFbaLTik2lfWPs9k3EnahdooZjJga3hH_oDaFUzNDDAaO-_rvQKHFR3k4GE-PoQok4JS_ujq4-kJijjMxdNhP-5hI"
                    ),
                    Exercise(
                            id = "2",
                            name = "Conventional Deadlift",
                            muscleGroup = MuscleGroup.BACK,
                            equipment = Equipment.BARBELL,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuA5r1kHhhnc3msn54F5v4pZx83AeFZgNsqBkB-3jTg1HDKEHBcZ9cEsPNL3TFmmJdi2NcBQXGrnk10hmDSJqp2KpDjnRGqKStFq8u7odw6olvGnYiulsrBGzJLCtABcbA9zxf9caNbG0yfybo2Uu372BPMGet01PBz1dtMA6OpWf6PrdgMRK9_1kJeSqzemC_jr6cAvqWXE4lkGmwa_sqt05mLZ2Qc_nF8f4HvCD5DegPWrTJbvh9QBkEwFr13gCnge15cevBoyig0"
                    ),
                    Exercise(
                            id = "3",
                            name = "Pull Up",
                            muscleGroup = MuscleGroup.BACK,
                            equipment = Equipment.BODYWEIGHT,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuCPI7wU_1dtsiabv1unszWxgB_HXsB2GWz3pbk4HlWgZ8-8XaTrRWKqCv6QZK2K4vGjVQe85CgRG6v_RnUjcv5L65_O2i_uFW-w57M6PE2UJwjqVvv-B9JY5h8bymQRxj73OsKEeYt4NpqjLw1kYgihOT3eC9oiGGCn2Y2BsFxeEu8ijOI3oQ-p0D4quDxvC0NrzWvnmNSUBgJD88d3EcJaAboIlacQ74WnW_mGXgKpbIF3z8QYDT--PFwDxJ4JfHgIv204nfkpUkU"
                    ),
                    Exercise(
                            id = "4",
                            name = "Arnold Press",
                            muscleGroup = MuscleGroup.SHOULDERS,
                            equipment = Equipment.DUMBBELL,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuDTZTfpIwLPQcBzghnH9jiM_TvFpmfWprFAhUF9li2eAtsjSlU-V0mUXimjQO9O9IBfiyYDNj-dNGZogaDOEdTEhjKrDh0u9_M5pQWciRq2rC9CLLXX0qm159u7156DeSscjg7zzOYEET_U8D9TK2-0mtc3ZKaGm7gTvlt6NNHQlzYUPTCIkXl5ZIWg2vqT9s8WK9pniGzKKLPVI_GqI5et-_kS4rkUkkY8vCdhcgHh5KcWXAIvvtPMgrPgp7jajAVIGq8rXgLzEbM"
                    ),
                    Exercise(
                            id = "5",
                            name = "Bicep Curl",
                            muscleGroup = MuscleGroup.ARMS,
                            equipment = Equipment.DUMBBELL,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuDsZJHUzqZLFESuwodpjP89zwcLasHOO8BMsjb3Rp32KqjKMeuOC9rbbd5fkoGFm3WrokLf1ykfUeBbW6pDXVpeBd0-Ih1GeohOrDw450itcUVwq58OwTbnf73wNhOaQ1xHESEOrWfJo9FVE0Sg79phyL_Xyl0QRfUyHXLBd1ObmxPj_isKVElvf817sQMlBCvtfCQiyRfWxXOawJF5HKrCqisxDm5UuHJhmRSCuQBIOvnah6CbedDVaNPYv7p6Y5Yc2mQ_Q6fa-Ck"
                    ),
                    Exercise(
                            id = "6",
                            name = "Cable Fly",
                            muscleGroup = MuscleGroup.CHEST,
                            equipment = Equipment.CABLE,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuCpkASJQns2Y7lceKpWt8xPImpyRh-0TShgEgZxX9yPuOs1T-D7l4EvXoP27NqUbD4POPJR-cUMzaQR_XiTWVkK1jEKuOXzchHtpntyz59bD9L6JfTu7PfL_tF2TElQRzVM5ROQKqS4-NaWSB_KHDgHdpNPr3JsEK54rC1kmEmF4o2NmQmPUnUQpu0oHjQsIpKdRt3sABd1pAv6sJEnBlGcNLCtdN_HkidnvckCJP24n7_s7E5qo6kaT2qahKLLmtpbBw7uZfMKzI8"
                    ),
                    Exercise(
                            id = "7",
                            name = "Back Squat",
                            muscleGroup = MuscleGroup.LEGS,
                            equipment = Equipment.BARBELL,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuB-iO-Hbr8vHva3MGszAPSPNczOCTKDcLr2IHvXx1jd4gXZ_ML6BOOvQIvulCORmkMDbzDXymbfFkrzhdnlP_a3COhkdLnkqmAPDr82mKAdiZ_cQJUdpS0Zjw-RgjuQGJFSbuIXhrryw6wQOPLFWfsdNqxTSvvlUv-NpMXUTQjaYsY704FH51veVkudtyJ1Wf4XkGMlH73bd4Lau5lT9ppdLwdkLyxQPGMw58cuL03ERlwcjImIUiY4yk4ZZKoOOrUOlhJ2O26Ntng"
                    ),
                    Exercise(
                            id = "8",
                            name = "Tricep Pushdown",
                            muscleGroup = MuscleGroup.ARMS,
                            equipment = Equipment.CABLE,
                            imageUrl =
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuAOJQYw6LLv-oeWVPvGmokhWr510lKbzM0kCHa23L0JM9pH7byFeq7PeaEDGJXLcPjC8vBk7F7n6UnaEaUs7GWDzVlbKqKNoT8n-k0fjgMidxgmzRGInojFisY-gmQCvbx7dXoM_XKMfaw5q-4fErogRvfaUgG5x0UtMGmB4stoLDVtubYhvOv8q9Qixyyhtgdqinb49sZzR8-aouJBxsU2oOH8O0rdeh2W8I3QKse4Wpd-PAap9bWEgz89fKO2ZVrh__DIJJFg2AA"
                    )
            )

    val uiState: StateFlow<LibraryUiState> =
            combine(_selectedMuscleGroup, _searchQuery) { selectedGroup: MuscleGroup, query: String
                        ->
                        val filtered =
                                allExercises.filter { exercise ->
                                    val matchesGroup =
                                            selectedGroup == MuscleGroup.ALL ||
                                                    exercise.muscleGroup == selectedGroup
                                    val matchesQuery =
                                            exercise.name.contains(query, ignoreCase = true) ||
                                                    exercise.muscleGroup.name.contains(
                                                            query,
                                                            ignoreCase = true
                                                    )
                                    matchesGroup && matchesQuery
                                }
                        LibraryUiState(
                                popularExercises = allExercises.take(3),
                                filteredExercises = filtered,
                                selectedMuscleGroup = selectedGroup,
                                searchQuery = query
                        )
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue =
                                    LibraryUiState(
                                            popularExercises = allExercises.take(3),
                                            filteredExercises = allExercises
                                    )
                    )

    fun onMuscleGroupSelected(group: MuscleGroup) {
        _selectedMuscleGroup.value = group
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
