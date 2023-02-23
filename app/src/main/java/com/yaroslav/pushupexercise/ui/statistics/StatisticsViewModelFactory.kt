package com.yaroslav.pushupexercise.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yaroslav.pushupexercise.ui.add.AddViewModel
import javax.inject.Inject
import javax.inject.Provider


class StatisticsViewModelFactory @Inject constructor(
    myViewModelProvider: Provider<StatisticsViewModel>
) : ViewModelProvider.Factory {
    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        StatisticsViewModel::class.java to myViewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}