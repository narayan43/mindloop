package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reelId: Long = 0,
    val exam: String = "UPSI",
    val subject: String,
    val chapter: String,
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val localCachePath: String? = null,
    val uploadedBy: String = "admin", // "admin" or "student"
    val durationSeconds: Int = 30,
    val watchCount: Int = 0,
    val timesWatched: Int = 0,
    val totalWatchTimeSeconds: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
