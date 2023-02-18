package com.yaroslav.pushupexercise.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslav.pushupexercise.data.PushUpRepository
import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class AddViewModel @Inject constructor(
    private val pushUpRepository: PushUpRepository
) : ViewModel() {

    private val _countPushUpsStateFlow = MutableStateFlow<Int>(0)
    val countPushUpsState = _countPushUpsStateFlow.asStateFlow()

    private val _timeStateFlow = MutableStateFlow<Int>(0)
    val timeState = _timeStateFlow.asStateFlow()


    //create new data or get data with room
    fun getData(id: Int, dateSeconds: Int) {
        //todo replace valid if default
        if (id < 0) {
            _countPushUpsStateFlow.value = pushUpRepository.getLastCountPushUp()
            _timeStateFlow.value = dateSeconds//(Date().time / 1000).toInt()
        } else {
            viewModelScope.launch {
                pushUpRepository.getPushUpById(id).collect {
                    _countPushUpsStateFlow.value = it.countPushUps
                    _timeStateFlow.value = it.recordTime
                }
            }
        }
    }

    fun addCount() {
        //todo move 5 to repo (write in repo use settings)
        if (_countPushUpsStateFlow.value + 5 <= 9999) {
            _countPushUpsStateFlow.value = _countPushUpsStateFlow.value + 5
        }
    }

    fun minusCount() {
        //todo move 5 to repo (write in repo use settings)
        if (_countPushUpsStateFlow.value - 5 > 0) {
            _countPushUpsStateFlow.value = _countPushUpsStateFlow.value - 5
        }
    }

    fun updateCount(count: Int) {
        _countPushUpsStateFlow.value = count
    }

    fun updateTime(time: Int) {
        _timeStateFlow.value = time
    }

    fun savePushUp(id: Int) {

        val mId = if (id < 0) null else id
        viewModelScope.launch {
            //id null == new record, id 1 == update record by id 1
            pushUpRepository.addPushUp(
                PushUp(
                    mId,
                    _countPushUpsStateFlow.value,
                    _timeStateFlow.value
                )
            )
            //save in shared prefs
            pushUpRepository.saveLastCountPushUp(_countPushUpsStateFlow.value)
        }
    }


}