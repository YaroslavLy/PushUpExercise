package com.yaroslav.pushupexercise.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslav.pushupexercise.data.PushUpRepository
import com.yaroslav.pushupexercise.models.PushUpSum
import com.yaroslav.pushupexercise.models.PushUpSumMonth
import com.yaroslav.pushupexercise.models.PushUpSumYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(
    private val pushUpRepository: PushUpRepository
) : ViewModel() {

    private var _periodOptions = PeriodOptions.DAILY
    val periodOptions : PeriodOptions
        get() = _periodOptions

    private val _pushUpsSumDailyStateFlow = MutableStateFlow<List<PushUpSum>>(emptyList())
    val pushUpsSumDailyState = _pushUpsSumDailyStateFlow.asStateFlow()

    private val _pushUpsSumWeeklyStateFlow = MutableStateFlow<List<PushUpSum>>(emptyList())
    val pushUpsSumWeeklyState = _pushUpsSumWeeklyStateFlow.asStateFlow()

    //MONTHLY
    private val _pushUpsSumMonthlyStateFlow = MutableStateFlow<List<PushUpSumMonth>>(emptyList())
    val pushUpsSumMonthlyState = _pushUpsSumMonthlyStateFlow.asStateFlow()

    //YEARLY
    private val _pushUpsSumYearlyStateFlow = MutableStateFlow<List<PushUpSumYear>>(emptyList())
    val pushUpsSumYearlyState = _pushUpsSumYearlyStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            pushUpRepository.getPushUpsDaily().collect {
                _pushUpsSumDailyStateFlow.value = it
            }
        }
    }

    fun getDailyData(){
        viewModelScope.launch {
            pushUpRepository.getPushUpsDaily().collect {
                //_pushUpsSumStateFlow.value = emptyList()
                _pushUpsSumDailyStateFlow.value = it
            }
        }
    }

    //WEEKLY
    fun getWeeklyData(){
        viewModelScope.launch {
            //_pushUpsSumStateFlow.value = emptyList()
            pushUpRepository.getPushUpsWeekly().collect{
                //_pushUpsSumStateFlow.value = emptyList()
                //_pushUpsSumStateFlow.value = it.map {it2 -> PushUpSum(it2.sumCountPushUps,it2.recordTime) }
                _pushUpsSumWeeklyStateFlow.value =it.map {it2 -> PushUpSum(it2.sumCountPushUps,it2.recordTime) }
            }
        }
    }

    fun getMonthlyData(){
        viewModelScope.launch {
             pushUpRepository.getPushUpsByMonth().collect{
                _pushUpsSumMonthlyStateFlow.value = it
            }
        }
    }

    fun getYearlyData(){
        viewModelScope.launch {
            pushUpRepository.getPushUpsByYear().collect{
                _pushUpsSumYearlyStateFlow.value = it
            }
        }
    }

    fun setperiodOptions(periodOptions: PeriodOptions) {
        _periodOptions = periodOptions
    }


//    fun clearData() {
//        viewModelScope.launch {
//            _pushUpsSumDailyStateFlow.value = emptyList()
//        }
//    }

}