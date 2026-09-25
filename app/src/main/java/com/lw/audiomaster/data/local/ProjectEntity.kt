package com.lw.audiomaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourceName: String,
    val durationMs: Long,
    val presetId: String,
    val presetName: String,
    val makeupGainDb: Float,
    val format: String,
    val createdAt: Long = System.currentTimeMillis(),
    val exported: Boolean = false,
    val outputPath: String? = null
)
