package com.yaroslav.pushupexercise.data.resource

import com.yaroslav.pushupexercise.data.PushUpsSource
import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PushUpSourceRoom @Inject constructor(
    private val pushUpDao: PushUpDao
) : PushUpsSource {

    override fun getPushUps(): Flow<List<PushUp>> = pushUpDao.getPushUps()

    override suspend fun addPushUp(pushUp: PushUp) = pushUpDao.insert(pushUp)
}