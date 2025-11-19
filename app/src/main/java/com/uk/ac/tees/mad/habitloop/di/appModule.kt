package com.uk.ac.tees.mad.habitloop.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.habitloop.data.AuthRepositoryImp
import com.uk.ac.tees.mad.habitloop.data.HabitLoopRepositoryImp
import com.uk.ac.tees.mad.habitloop.data.local.HabitLoopDatabase
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.presentation.add_habbit.AddHabbitViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.create_account.CreateAccountViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.forgot.ForgotViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.login.LoginViewModel
import com.uk.ac.tees.mad.habitloop.presentation.dashboard.DashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Room Database
    single {
        Room.databaseBuilder(
            get(),
            HabitLoopDatabase::class.java,
            "habitloop_db"
        ).build()
    }

    // Dao
    single { get<HabitLoopDatabase>().habitDao() }

    // Repositories
    single<HabitLoopRepository> { HabitLoopRepositoryImp(get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImp(get()) }

    // ViewModels
    viewModel { DashboardViewModel(get()) }
    viewModel { AddHabbitViewModel(get()) }
    viewModel { CreateAccountViewModel(get(), get()) }
    viewModel { ForgotViewModel(get()) }
    viewModel { LoginViewModel(get()) }
}
