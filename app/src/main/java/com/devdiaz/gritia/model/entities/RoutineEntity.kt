package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "routines",
        foreignKeys =
                [
                        ForeignKey(
                                entity = UserEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["user_id"],
                                onDelete = ForeignKey.CASCADE
                        )]
)
data class RoutineEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "user_id", index = true) val userId: Long,
        val name: String,
        val monday: Boolean = false,
        val tuesday: Boolean = false,
        val wednesday: Boolean = false,
        val thursday: Boolean = false,
        val friday: Boolean = false,
        val saturday: Boolean = false,
        val sunday: Boolean = false,
        @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
