package com.yaroslav.pushupexercise.data.resource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yaroslav.pushupexercise.models.PushUp

@Database(entities = arrayOf(PushUp::class), version = 1, exportSchema = false)
public abstract class PushUpRoomDatabase : RoomDatabase() {

    abstract fun pushUpDao(): PushUpDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PushUpRoomDatabase? = null

        fun getDatabase(context: Context): PushUpRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PushUpRoomDatabase::class.java,
                    "car_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}