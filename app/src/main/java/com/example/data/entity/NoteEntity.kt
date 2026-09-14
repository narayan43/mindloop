package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val examId: String = "UPSI",
    val subjectName: String,
    val chapterName: String,
    val chapterNumber: Int,
    val title: String,
    val summaryText: String,
    val imageUri: String? = null,
    val revisitCount: Int = 1,
    val timeSpentSeconds: Long = 134,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
