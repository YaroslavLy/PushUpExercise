package com.yaroslav.pushupexercise.models

import androidx.room.ColumnInfo

data class PushUpSumMonth (
    //week_number
    @ColumnInfo(name = "month") val monthNumber: String,
    @ColumnInfo(name = "total_push_ups") val sumCountPushUps: Int,
    //@ColumnInfo(name = "record_time") val recordTime: Int
)