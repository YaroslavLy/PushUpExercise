package com.yaroslav.pushupexercise.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslav.pushupexercise.data.PushUpRepository
import com.yaroslav.pushupexercise.models.PushUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val pushUpRepository: PushUpRepository
    ) : ViewModel() {

    private val _pushUpsStateFlow = MutableStateFlow<List<PushUp>>(emptyList())
    val pushUpsState = _pushUpsStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            pushUpRepository.getPushUps().collect {
                _pushUpsStateFlow.value = it
            }
        }
    }

}