package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE subjectName = :subjectName AND chapterName = :chapterName ORDER BY id ASC")
    fun getNotesByChapter(subjectName: String, chapterName: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE subjectName = :subjectName ORDER BY chapterNumber ASC, id ASC")
    fun getNotesBySubject(subjectName: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteByIdDirect(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET revisitCount = revisitCount + 1, timeSpentSeconds = timeSpentSeconds + :additionalSeconds, lastReadTimestamp = :now WHERE id = :noteId")
    suspend fun recordStudySession(noteId: Long, additionalSeconds: Long, now: Long = System.currentTimeMillis())

    @Query("SELECT * FROM notes ORDER BY revisitCount DESC LIMIT :limit")
    fun getMostRevisitedNotes(limit: Int = 3): Flow<List<NoteEntity>>
}
