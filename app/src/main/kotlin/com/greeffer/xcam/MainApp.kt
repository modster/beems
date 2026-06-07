package com.greeffer.xcam

import android.app.Application
import androidx.lifecycle.ViewModel
import com.greeffer.xcam.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

// Android Application class
class MyApplication: Application()
{


    override fun onCreate()
    {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(appModule)
        }
    }
}


// ViewModel for the main screen
class MainViewModel: ViewModel()
{
    // TODO: Add state and business logic
}

// Compose UI uses Koin automatically
// @Composable
// fun App()
// {
//     val viewModel = koinViewModel<MainViewModel>()
// }
