package com.yaroslav.pushupexercise.data

interface CountDataSource {
    fun saveLastCount(lastCount: Int)

    fun getLastCount():Int
}