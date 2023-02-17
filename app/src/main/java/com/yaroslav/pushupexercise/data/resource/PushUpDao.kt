package com.yaroslav.pushupexercise.data.resource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.Flow

@Dao
interface PushUpDao {

    @Query("SELECT * FROM push_up " +
            "WHERE record_time >= :startDay AND record_time < :startDay+86400 "+
            "ORDER BY record_time ASC")
    fun getPushUpsForToday(startDay: Int): Flow<List<PushUp>>

    @Query("SELECT * FROM push_up WHERE id = :id")
    fun getPushUpById(id: Int): Flow<PushUp>

    //OnConflictStrategy combine
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pushUp: PushUp)
}