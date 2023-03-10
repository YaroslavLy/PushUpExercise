package com.yaroslav.pushupexercise

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.yaroslav.pushupexercise.di.AppComponent
import com.yaroslav.pushupexercise.di.AppModule
import com.yaroslav.pushupexercise.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build() //create()
    }

}

val Fragment.appComponent : AppComponent
    get() = requireContext().appComponent

val Context.appComponent : AppComponent
    get() = when(this){
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }