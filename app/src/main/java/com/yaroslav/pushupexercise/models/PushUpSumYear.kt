package com.yaroslav.pushupexercise.models

import androidx.room.ColumnInfo

data class PushUpSumYear(
    val year: String,
    @ColumnInfo(name = "total_push_ups") val totalPushUps: Int
)