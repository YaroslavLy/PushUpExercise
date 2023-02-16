package com.yaroslav.pushupexercise.data

import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PushUpRepository @Inject constructor(private val pusUpsSource: PushUpsSource) {

    fun getPushUps(): Flow<List<PushUp>> = pusUpsSource.getPushUps()

    suspend fun addPushUp(pushUp: PushUp) = pusUpsSource.addPushUp(pushUp)

}