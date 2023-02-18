package com.yaroslav.pushupexercise.data

import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.Flow

interface PushUpsSource {

    fun getPushUpsForToday(startDay: Int): Flow<List<PushUp>>

    suspend fun addPushUp(pushUp: PushUp)

    fun getPushUpById(id: Int): Flow<PushUp>

    suspend  fun deletePushUpById(id: Int)
    //fun getCar(id: Int): Flow<Car>
    //suspend fun deleteCar(id: Int)
    //suspend fun  updateCar(id: Int,car: Car)
    //suspend fun addNewCar()
}