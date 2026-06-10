package com.greeffer.xcam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.greeffer.xcam.ui.theme.XCamTheme

class MainActivity: ComponentActivity()
{


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            XCamTheme {
                Surface(
                  modifier = Modifier.fillMaxSize(),
                ) { MainNavigation() }
            }
        }
    }
}
