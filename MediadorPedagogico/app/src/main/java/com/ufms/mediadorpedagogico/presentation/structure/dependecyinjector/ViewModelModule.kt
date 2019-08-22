package com.ufms.mediadorpedagogico.presentation.structure.dependecyinjector

import com.ufms.mediadorpedagogico.presentation.landing.SplashViewModel
import com.ufms.mediadorpedagogico.presentation.login.LoginViewModel
import com.ufms.mediadorpedagogico.presentation.password.RecoverPasswordViewModel
import com.ufms.mediadorpedagogico.presentation.signup.SignUpViewModel
import com.ufms.mediadorpedagogico.presentation.structure.base.BaseViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BaseViewModel() }
    viewModel { RecoverPasswordViewModel(get(), get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
}