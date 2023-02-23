package com.yaroslav.pushupexercise.models

import androidx.room.ColumnInfo

data class PushUpSumWeek (
    //week_number
    @ColumnInfo(name = "week_number") val weekNumber: String,
    @ColumnInfo(name = "count_push_ups") val sumCountPushUps: Int,
    @ColumnInfo(name = "record_time") val recordTime: Int
    )