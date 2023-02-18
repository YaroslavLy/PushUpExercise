package com.yaroslav.pushupexercise.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslav.pushupexercise.data.PushUpRepository
import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val pushUpRepository: PushUpRepository
) : ViewModel() {

    //push ups in day
    private val _pushUpsStateFlow = MutableStateFlow<List<PushUp>>(emptyList())
    val pushUpsState = _pushUpsStateFlow.asStateFlow()

    //sum count push ups
    val pushUpsCountStateFlow: StateFlow<Int> = _pushUpsStateFlow
        .map { pushUps -> pushUps.sumOf { it.countPushUps } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    //use data(in ui) but have time (not contain milliseconds)
    private val _dateStateFlow = MutableStateFlow<Int>((Date().time / 1000).toInt())
    val dateState = _dateStateFlow.asStateFlow()



    init {
        viewModelScope.launch {
            //todo move to utils
            val calendar = Calendar.getInstance() // Get a Calendar instance
            calendar.set(Calendar.HOUR_OF_DAY, 0) // Set the hour to 0
            calendar.set(Calendar.MINUTE, 0) // Set the minute to 0
            calendar.set(Calendar.SECOND, 0) // Set the second to 0
            calendar.set(Calendar.MILLISECOND, 0) // Set the millisecond to 0
            val startOfDayInMillis = calendar.timeInMillis // Get the time in milliseconds
            val startDay = (startOfDayInMillis / 1000).toInt()

            pushUpRepository.getPushUpsForToday(startDay).collect {
                _pushUpsStateFlow.value = it
            }
        }
    }

    // action == -1 - minus 1 day, action == 0 - set data use dateTime, action == 1 - add 1 day
    fun updateDate(dateTime: Int , action: Int) {
        when (action) {
            0 -> {
                _dateStateFlow.value = dateTime
            }
            1 -> {
                if (_dateStateFlow.value + 86400 < (Date().time / 1000).toInt())
                    _dateStateFlow.value = _dateStateFlow.value + 86400
            }
            -1 -> {
                _dateStateFlow.value = _dateStateFlow.value - 86400
            }
            else -> {}
        }
        updatePushUps()
    }

    private fun updatePushUps(){
        viewModelScope.launch {
            //todo move to utils
            val calendar = Calendar.getInstance() // Get a Calendar instance
            calendar.set(Calendar.HOUR_OF_DAY, 0) // Set the hour to 0
            calendar.set(Calendar.MINUTE, 0) // Set the minute to 0
            calendar.set(Calendar.SECOND, 0) // Set the second to 0
            calendar.set(Calendar.MILLISECOND, 0) // Set the millisecond to 0
            val startOfDayInMillis = calendar.timeInMillis // Get the time in milliseconds
            val dif = Date().time - calendar.timeInMillis
            val difInSeconds = dif.toInt() / 1000


            pushUpRepository.getPushUpsForToday(_dateStateFlow.value-difInSeconds).collect {
                _pushUpsStateFlow.value = it
            }
        }
    }


    fun deletePushUpById(id: Int) {
        viewModelScope.launch {
            pushUpRepository.deletePushUpById(id)
        }
    }

}