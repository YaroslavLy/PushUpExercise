package com.yaroslav.pushupexercise.di

import android.content.Context
import com.yaroslav.pushupexercise.MainActivity
import com.yaroslav.pushupexercise.ui.add.AddViewModelFactory
import com.yaroslav.pushupexercise.ui.main.MainFragment
import com.yaroslav.pushupexercise.ui.main.MainViewModel
import com.yaroslav.pushupexercise.ui.main.ViewModelFactory
import com.yaroslav.pushupexercise.ui.statistics.StatisticsViewModel
import com.yaroslav.pushupexercise.ui.statistics.StatisticsViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,DataModule::class,DataBinds::class])
interface AppComponent {

    fun myViewModel(): MainViewModel

    fun viewModelsFactory(): ViewModelFactory

    fun addViewModelsFactory(): AddViewModelFactory

    fun statisticsViewModelsFactory(): StatisticsViewModelFactory

    fun context(): Context

}