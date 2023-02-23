package com.yaroslav.pushupexercise.data.resource

import com.yaroslav.pushupexercise.data.PushUpsSource
import com.yaroslav.pushupexercise.models.PushUp
import com.yaroslav.pushupexercise.models.PushUpSum
import com.yaroslav.pushupexercise.models.PushUpSumYear
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PushUpSourceRoom @Inject constructor(
    private val pushUpDao: PushUpDao
) : PushUpsSource {

    override fun getPushUpsForToday(startDay: Int): Flow<List<PushUp>> =
        pushUpDao.getPushUpsForToday(startDay)

    override suspend fun addPushUp(pushUp: PushUp) = pushUpDao.insert(pushUp)

    override fun getPushUpById(id: Int) = pushUpDao.getPushUpById(id)

    override suspend fun deletePushUpById(id: Int) = pushUpDao.deletePushUpById(id)

    override fun getPushUpsDaily() = pushUpDao.getPushUpsDaily()

    override fun getPushUpsWeekly() = pushUpDao.getPushUpsWeekly()

    override fun getPushUpsByMonth() = pushUpDao.getPushUpsByMonth()

    override fun getPushUpsByYear() = pushUpDao.getPushUpsByYear()
}