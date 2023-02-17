package com.yaroslav.pushupexercise.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yaroslav.pushupexercise.ui.main.MainViewModel
import javax.inject.Inject
import javax.inject.Provider

class AddViewModelFactory @Inject constructor(
    myViewModelProvider: Provider<AddViewModel>
) : ViewModelProvider.Factory {
    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        AddViewModel::class.java to myViewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}