package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ReelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY id ASC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE exam = :exam ORDER BY id ASC")
    fun getReelsByExam(exam: String): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE exam = :exam AND subject = :subject ORDER BY id ASC")
    fun getReelsBySubject(exam: String, subject: String): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE exam = :exam AND subject = :subject AND chapter = :chapter ORDER BY id ASC")
    fun getReelsByChapter(exam: String, subject: String, chapter: String): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE id = :id LIMIT 1")
    suspend fun getReelById(id: Long): ReelEntity?

    @Query("SELECT * FROM reels WHERE reelId = :reelId LIMIT 1")
    suspend fun getReelByReelId(reelId: Long): ReelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reels: List<ReelEntity>)

    @Update
    suspend fun updateReel(reel: ReelEntity)

    @Query("UPDATE reels SET timesWatched = timesWatched + 1, watchCount = watchCount + 1, totalWatchTimeSeconds = totalWatchTimeSeconds + :watchSeconds WHERE id = :id")
    suspend fun incrementReelWatch(id: Long, watchSeconds: Long)

    @Query("UPDATE reels SET localCachePath = :cachePath WHERE id = :id")
    suspend fun updateLocalCachePath(id: Long, cachePath: String)

    @Query("SELECT COUNT(*) FROM reels")
    suspend fun getReelCount(): Int
}
