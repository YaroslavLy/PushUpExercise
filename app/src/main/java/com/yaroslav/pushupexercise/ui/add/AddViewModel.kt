package com.yaroslav.pushupexercise.ui.add

import androidx.lifecycle.ViewModel
import com.yaroslav.pushupexercise.data.PushUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

class AddViewModel @Inject constructor(
     private val pushUpRepository: PushUpRepository
): ViewModel() {

     private val _countPushUpsStateFlow = MutableStateFlow<Int>(0)
     val countPushUpsState = _countPushUpsStateFlow.asStateFlow()

     private val _timeStateFlow = MutableStateFlow<Int>(0)
     val timeState = _timeStateFlow.asStateFlow()


     fun getData(id: Int){
          //todo replece valid if default
          if(id<0){
               _countPushUpsStateFlow.value = pushUpRepository.getLastCountPushUp()
               _timeStateFlow.value = (Date().time / 1000).toInt()
          }else {
               TODO()
          }
     }

}