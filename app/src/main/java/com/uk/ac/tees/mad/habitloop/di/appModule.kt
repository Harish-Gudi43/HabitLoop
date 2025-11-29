package com.uk.ac.tees.mad.habitloop.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.habitloop.data.AuthRepositoryImpl
import com.uk.ac.tees.mad.habitloop.data.HabitLoopRepositoryImp
import com.uk.ac.tees.mad.habitloop.data.QuoteRepositoryImp
import com.uk.ac.tees.mad.habitloop.data.SupabaseStorageRepositoryImpl
import com.uk.ac.tees.mad.habitloop.data.local.HabitLoopDatabase
import com.uk.ac.tees.mad.habitloop.data.notification.NotificationScheduler
import com.uk.ac.tees.mad.habitloop.data.remote.QuoteApi
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.QuoteRepository
import com.uk.ac.tees.mad.habitloop.domain.SupabaseStorageRepository
import com.uk.ac.tees.mad.habitloop.presentation.add_habbit.AddHabbitViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.create_account.CreateAccountViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.forgot.ForgotViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.login.LoginViewModel
import com.uk.ac.tees.mad.habitloop.presentation.dashboard.DashboardViewModel
import com.uk.ac.tees.mad.habitloop.presentation.profile.ProfileViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Ktor Client
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    // API
    single { QuoteApi(get()) }

    // Supabase
    single {
        createSupabaseClient(
            supabaseUrl = "YOUR_SUPABASE_URL",
            supabaseKey = "YOUR_SUPABASE_KEY"
        ) {
            install(Storage)
        }
    }
    single { get<SupabaseClient>().storage }


    // Room Database
    single {
        Room.databaseBuilder(
            get(),
            HabitLoopDatabase::class.java,
            "habitloop_db"
        ).fallbackToDestructiveMigration().build()
    }

    // Dao
    single { get<HabitLoopDatabase>().habitDao() }
    single { get<HabitLoopDatabase>().quoteDao() }
    single { get<HabitLoopDatabase>().userDao() }

    // Repositories
    single<HabitLoopRepository> { HabitLoopRepositoryImp(get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<QuoteRepository> { QuoteRepositoryImp(get(), get()) }
    single<SupabaseStorageRepository> { SupabaseStorageRepositoryImpl(get()) }

    // Notification
    single { NotificationScheduler(androidContext()) }

    // ViewModels
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { params -> AddHabbitViewModel(get(), get(), params.get()) }
    viewModel { CreateAccountViewModel(get()) }
    viewModel { ForgotViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}
