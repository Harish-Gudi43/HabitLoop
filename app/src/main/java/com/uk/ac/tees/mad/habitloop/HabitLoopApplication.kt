package com.uk.ac.tees.mad.habitloop

import android.app.Application
import com.uk.ac.tees.mad.habitloop.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HabitLoopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HabitLoopApplication)
            modules(appModule)
        }
    }
}
