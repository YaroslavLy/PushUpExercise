package com.yaroslav.pushupexercise.data.resource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.Flow

@Dao
interface PushUpDao {

    @Query("SELECT * FROM push_up ORDER BY record_time ASC")
    fun getPushUps(): Flow<List<PushUp>>

    //OnConflictStrategy combine
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pushUp: PushUp)
}