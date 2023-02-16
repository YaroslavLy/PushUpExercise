package com.yaroslav.pushupexercise.di

import android.content.Context
import dagger.Module
import dagger.Provides


@Module
class AppModule(val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

//    fun provideMainViewModelFactory(pushUpRepository: PushUpRepository): MainViewModelFactory {
//        return MainViewModelFactory(pushUpRepository = pushUpRepository)
//    }

}