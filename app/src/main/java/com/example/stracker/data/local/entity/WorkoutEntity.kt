package com.example.stracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startedAt: Long, // timestamp in millis
    val finishedAt: Long? = null,
    val note: String? = null,
    val isCompleted: Boolean = false
)
