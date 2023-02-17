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
    val pushUpRepository: PushUpRepository
    ) : ViewModel() {

    private val _pushUpsStateFlow = MutableStateFlow<List<PushUp>>(emptyList())
    val pushUpsState = _pushUpsStateFlow.asStateFlow()

    val pushUpsCountStateFlow: StateFlow<Int> = _pushUpsStateFlow
        .map { pushUps -> pushUps.sumOf { it.countPushUps } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    init {
        viewModelScope.launch {
            //todo move to utils
            val calendar = Calendar.getInstance() // Get a Calendar instance
            calendar.set(Calendar.HOUR_OF_DAY, 0) // Set the hour to 0
            calendar.set(Calendar.MINUTE, 0) // Set the minute to 0
            calendar.set(Calendar.SECOND, 0) // Set the second to 0
            calendar.set(Calendar.MILLISECOND, 0) // Set the millisecond to 0
            val startOfDayInMillis = calendar.timeInMillis // Get the time in milliseconds
            val startDay = (startOfDayInMillis/1000).toInt()

            pushUpRepository.getPushUpsForToday(startDay).collect {
                _pushUpsStateFlow.value = it
            }
        }
    }

}