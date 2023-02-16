package com.yaroslav.pushupexercise.di

import android.content.Context
import com.yaroslav.pushupexercise.data.PushUpsSource
import com.yaroslav.pushupexercise.data.PushUpRepository
import com.yaroslav.pushupexercise.data.resource.PushUpDao
import com.yaroslav.pushupexercise.data.resource.PushUpRoomDatabase
import com.yaroslav.pushupexercise.data.resource.PushUpSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
class DataModule {

    @Provides
    fun provideCarDao(database: PushUpRoomDatabase): PushUpDao{
        return database.pushUpDao()
    }

//    @Provides
//    fun providePushUpRepository(pusUpsSource: PushUpsSource): PushUpRepository {
//        return PushUpRepository(pusUpsSource)
//    }

    @Provides
    fun providePushUpRoomDatabase(context: Context):PushUpRoomDatabase{
        return PushUpRoomDatabase.getDatabase(context = context)
    }
}