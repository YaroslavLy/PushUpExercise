package com.yaroslav.pushupexercise.di

import com.yaroslav.pushupexercise.data.CountDataSource
import com.yaroslav.pushupexercise.data.CountDataSourceSharedPrefs
import com.yaroslav.pushupexercise.data.PushUpsSource
import com.yaroslav.pushupexercise.data.resource.PushUpSourceRoom
import dagger.Binds
import dagger.Module

@Module
interface DataBinds {

    @Binds
    fun bindsPushUpSourceToPushUpSourceRoom(
        pushUpSourceRoom: PushUpSourceRoom
    ): PushUpsSource

    @Binds
    fun bindsCountDataSourceToCountDataSourceSharedPrefs(
        countDataSourceSharedPrefs: CountDataSourceSharedPrefs
    ): CountDataSource
}