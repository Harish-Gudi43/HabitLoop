package com.uk.ac.tees.mad.habitloop.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class HabitLoopApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@HabitLoopApplication)
            androidLogger()
        }
    }
}
