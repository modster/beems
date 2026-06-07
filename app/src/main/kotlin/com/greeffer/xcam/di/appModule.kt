package com.greeffer.xcam.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.greeffer.xcam.ui.main.MainScreenViewModel
import org.koin.plugin.module.dsl.viewModel

import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    viewModel<MainScreenViewModel>()
}
