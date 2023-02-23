package com.yaroslav.pushupexercise.data

import com.yaroslav.pushupexercise.models.PushUp
import com.yaroslav.pushupexercise.models.PushUpSumYear
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PushUpRepository @Inject constructor(
    private val pusUpsSource: PushUpsSource,
    private val countDataSource: CountDataSource
    ) {

    fun getPushUpsForToday(startDay: Int): Flow<List<PushUp>> = pusUpsSource.getPushUpsForToday(startDay)

    suspend fun addPushUp(pushUp: PushUp) = pusUpsSource.addPushUp(pushUp)

    fun saveLastCountPushUp(lastCount: Int) = countDataSource.saveLastCount(lastCount)

    fun getLastCountPushUp():Int = countDataSource.getLastCount()

    fun getPushUpById(id: Int) = pusUpsSource.getPushUpById(id)

    suspend fun deletePushUpById(id: Int) = pusUpsSource.deletePushUpById(id)

    fun getPushUpsDaily() = pusUpsSource.getPushUpsDaily()

    //WEEKLY
    fun getPushUpsWeekly() = pusUpsSource.getPushUpsWeekly()

    fun getPushUpsByMonth() = pusUpsSource.getPushUpsByMonth()

    fun getPushUpsByYear() = pusUpsSource.getPushUpsByYear()


}