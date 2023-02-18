package com.yaroslav.pushupexercise.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="push_up")
data class PushUp(
    @PrimaryKey(autoGenerate = true) val id:Int?,
    @ColumnInfo(name = "count_push_ups") val countPushUps: Int,
    @ColumnInfo(name = "record_time") val recordTime: Int
)