package com.uk.ac.tees.mad.habitloop.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.habitloop.data.AuthRepositoryImpl
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.presentation.auth.create_account.CreateAccountViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.forgot.ForgotViewModel
import com.uk.ac.tees.mad.habitloop.presentation.auth.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.ac.tees.mad.bookly.domain.util.NetworkManager

val appModule = module{

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    viewModelOf(:: LoginViewModel)
    viewModelOf(::CreateAccountViewModel)
    viewModelOf(::ForgotViewModel)

    single { NetworkManager(androidContext()) }

}
