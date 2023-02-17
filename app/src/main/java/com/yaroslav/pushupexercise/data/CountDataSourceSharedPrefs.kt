package com.yaroslav.pushupexercise.data

import android.content.Context
import javax.inject.Inject


class CountDataSourceSharedPrefs @Inject constructor(context: Context):CountDataSource {

    private val sharedPreferences = context.getSharedPreferences("countData", Context.MODE_PRIVATE)

    override fun saveLastCount(lastCount: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(PREF_LAST_COUNT_PUSH_UP, lastCount)
        editor.apply()
    }

    override fun getLastCount(): Int {
        return sharedPreferences.getInt(PREF_LAST_COUNT_PUSH_UP, 20)
    }

    companion object {
        private const val PREF_LAST_COUNT_PUSH_UP = "lastCountPushUp"
    }
}