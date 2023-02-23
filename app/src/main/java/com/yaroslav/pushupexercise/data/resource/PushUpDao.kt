package com.yaroslav.pushupexercise.data.resource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yaroslav.pushupexercise.models.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PushUpDao {

    @Query("SELECT * FROM push_up " +
            "WHERE record_time >= :startDay AND record_time < :startDay+86400 "+
            "ORDER BY record_time ASC")
    fun getPushUpsForToday(startDay: Int): Flow<List<PushUp>>

    @Query("SELECT * FROM push_up WHERE id = :id")
    fun getPushUpById(id: Int): Flow<PushUp>

    @Query("DELETE FROM push_up WHERE id = :id")
    suspend fun deletePushUpById(id: Int)

    //OnConflictStrategy combine
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pushUp: PushUp)

    @Query("SELECT SUM(count_push_ups) AS sumCountPushUps, " +
            "record_time AS recordTime " +
            "FROM push_up GROUP BY " +
            "record_time/86400 LIMIT 50")
    fun getPushUpsDaily(): Flow<List<PushUpSum>>



    //@Query("SELECT SUM(count_push_ups) FROM push_up WHERE strftime('%Y-%W', datetime(record_time/1000, 'unixepoch', 'start of day', '-1 day')) = strftime('%Y-%W', 'now', 'localtime', 'start of day', '-1 day', 'weekday 1')")
    //fun getWeeklyCountPushUps(): Int

    //@Query("")
    //fun getWeeklyCountPushUps(): List<WeeklyCountPushUps>


    @Query("SELECT strftime('%Y-%W', datetime(record_time, 'unixepoch', 'start of day', '0 day')) as week_number, SUM(count_push_ups) as count_push_ups, record_time FROM push_up GROUP BY week_number LIMIT 50")
    fun getPushUpsWeekly(): Flow<List<PushUpSumWeek>>


    //@Query("SELECT strftime('%m', record_time , 'unixepoch') AS month, SUM(count_push_ups) AS total_push_ups,record_time FROM push_up GROUP BY month ")
    //fun getPushUpsByMonth(startOfYear: Long, endOfYear: Long): Flow<List<PushUpSumMonth>>

    @Query("SELECT strftime('%Y-%m', record_time, 'unixepoch') AS month, SUM(count_push_ups) AS total_push_ups FROM push_up GROUP BY month")
    fun getPushUpsByMonth(): Flow<List<PushUpSumMonth>>
    //

//    @Query("SELECT m.month, COALESCE(SUM(p.count_push_ups), 0) as count FROM (SELECT 1 as month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m LEFT JOIN (SELECT strftime('%m', record_time/1000, 'unixepoch') as month, count_push_ups FROM push_up WHERE record_time BETWEEN 1672531200357 AND 1704067199357) p ON m.month = p.month GROUP BY m.month")
//    fun getPushUpCountsByMonth(startOfYear: Long, endOfYear: Long): List<Pair<Int, Int>>


    @Query("SELECT strftime('%Y', record_time , 'unixepoch') as year, sum(count_push_ups) as total_push_ups FROM push_up GROUP BY year")
    fun getPushUpsByYear(): Flow<List<PushUpSumYear>>
}