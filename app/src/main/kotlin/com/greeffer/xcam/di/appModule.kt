package com.greeffer.xcam.di

import com.greeffer.xcam.data.DataRepository
import com.greeffer.xcam.data.XCameraFilterEntries
import com.greeffer.xcam.ui.main.MainRepository
import com.greeffer.xcam.ui.main.MainRepositoryImpl
import com.greeffer.xcam.ui.main.MainScreenViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    single<XCameraFilterEntries>() bind DataRepository::class
    single<MainRepositoryImpl>() bind MainRepository::class
    viewModel<MainScreenViewModel>()
}
