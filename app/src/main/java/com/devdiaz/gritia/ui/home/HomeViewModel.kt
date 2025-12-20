package com.devdiaz.gritia.ui.home

import androidx.lifecycle.ViewModel
import com.devdiaz.gritia.model.Routine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        _routines.value =
                listOf(
                        Routine(
                                title = "Push Day A",
                                muscles = "Chest • Triceps • Shoulders",
                                schedule =
                                        listOf(
                                                true,
                                                false,
                                                false,
                                                true,
                                                false,
                                                false,
                                                false
                                        ), // M, Th
                                imageUrl =
                                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDqY0KkUgicK3T7mKZ84WozL16D81mkB2DaXpQ3kVkXj-9CLwziIwg3bYBgNuflYPDcP4_bcKQcQ4dLVQJbjhxNtvWTKl5RDT22gvjR3hw62hMtGv_-7BmCtacjRU_urg6Mfs3yR6dpmq-WxD_wyx5jnFma3414ZaFOBej_K7RO0F6f-BcBh7hG8kw50JaVAf88Am69VSVf4zDBHKYVkl1O3zYPb7-tGFKTbOaLuo4WSwkbUJtCA1yc7bP01iHB8jIQ0M726n3vIB4"
                        ),
                        Routine(
                                title = "Pull Power",
                                muscles = "Back • Biceps",
                                schedule =
                                        listOf(
                                                false,
                                                true,
                                                false,
                                                false,
                                                true,
                                                false,
                                                false
                                        ), // T, F
                                imageUrl =
                                        "https://lh3.googleusercontent.com/aida-public/AB6AXuD22nAvZN9yzyoDlXKm3NjI3KWiSVB4yAe8poFOSvK9iI0tdFHGqkOoMAOHI-RPczFpbXTQseX369PhtvO9lFnIWv-_VQ3nxwcIsnNpemnEXpD0cK80D8C0rhw1pEbpYLHH5dD3fl2CjnTLPiFUWRI0FB1B7Dnlr5TFhNJaOTooHr6VmYC4qmsBQhD7e6ugaNXtriOBFsg92lBFAn-dzIvLQMd0086eBkbQ5SonQNGpnBzCfvPeA-Q9zRwRZOtaASk5BfvnDdo1huU"
                        ),
                        Routine(
                                title = "Leg Destruction",
                                muscles = "Quads • Hams • Calves",
                                schedule =
                                        listOf(
                                                false,
                                                false,
                                                true,
                                                false,
                                                false,
                                                true,
                                                false
                                        ), // W, S
                                imageUrl =
                                        "https://lh3.googleusercontent.com/aida-public/AB6AXuA3sCBQqji66a_9WbZNni3ZjVGvX5eEZdpmzYrjigmZujec3LU7iCxOPQQUZJrS833Qdd1RJtqSzg7eqd-YmHCvCMYC7InBhKW_r1P5TK8mDnzMmx7mXWuT3-G9MLYJRkzL7HKkIIeKCZvRa0mR1mEFyABe9Np5glENtaVDBc85nMStZuwCZXec_KQp63c7ImQR1_0tl5XMbOccH6_InE-JTIWuu68kP5sQJn4yokm5NDbMtT7jy0hHPnTirTVjH0eacSWfUkWbYsM"
                        ),
                        Routine(
                                title = "Morning Cardio",
                                muscles = "Endurance",
                                schedule =
                                        listOf(true, true, true, true, true, false, false), // M-F
                                imageUrl = "" // Special case for icon in design, handled in UI or
                                // here? Let's use empty for now and handle logic.
                                // Actually, the last one in design has a generic gradient/icon.
                                // I'll handle that in UI based on empty URL or add a flag.
                                // For simplicity, I'll pass a flag or just check if URL is empty.
                                )
                )
    }
}
