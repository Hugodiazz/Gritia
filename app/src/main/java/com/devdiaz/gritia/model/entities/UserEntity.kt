package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        val email: String,
        val gender: String?, // "Masculino", "Femenino", "Otro"
        @ColumnInfo(name = "birth_date") val birthDate: String?, // ISO-8601 YYYY-MM-DD
        val height: Float?,
        @ColumnInfo(name = "current_weight") val currentWeight: Float?,
        @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
