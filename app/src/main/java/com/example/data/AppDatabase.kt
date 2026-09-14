package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.NoteDao
import com.example.data.dao.QuestionAttemptDao
import com.example.data.dao.QuestionDao
import com.example.data.dao.ReelDao
import com.example.data.dao.StudyLogDao
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionAttemptEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.ReelEntity
import com.example.data.entity.StudySessionEntity

@Database(
    entities = [
        NoteEntity::class,
        QuestionEntity::class,
        StudySessionEntity::class,
        QuestionAttemptEntity::class,
        ReelEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun questionDao(): QuestionDao
    abstract fun questionAttemptDao(): QuestionAttemptDao
    abstract fun studyLogDao(): StudyLogDao
    abstract fun reelDao(): ReelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindloop_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
