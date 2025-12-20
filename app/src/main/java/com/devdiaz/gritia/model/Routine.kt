package com.devdiaz.gritia.model

data class Routine(
        val title: String,
        val muscles: String,
        val schedule: List<Boolean>, // 7 booleans for M T W T F S S
        val imageUrl: String
)
