package com.greeffer.xcam

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.greeffer.xcam.data.DefaultDataRepository
import com.greeffer.xcam.fx.x.XCameraScaffold
import com.greeffer.xcam.ui.main.XViewfinder
import com.greeffer.xcam.ui.main.MainScreen
import com.greeffer.xcam.ui.main.MainScreenViewModel

@Composable
fun MainNavigation()
{
    val backStack = rememberNavBackStack(Main)
    
    NavDisplay(backStack = backStack, onBack = { backStack.removeLastOrNull() }, entryProvider = entryProvider {
        entry<Main> {
            MainScreen(
                onItemClick = { navKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp)
            )
        }
        
        entry<XViewFinderX> {
            XViewfinder(
                onItemClick = { navKey: NavKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp),
                vm = viewModel { MainScreenViewModel(DefaultDataRepository()) }
            )
        }
        entry<XCamScreenX> {
            XViewfinder(
                onItemClick = { navKey: NavKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp),
                vm = viewModel { MainScreenViewModel(DefaultDataRepository()) }
            )
        }
        entry<XVignetterShaderProgramX> {
            XViewfinder(
                onItemClick = { navKey: NavKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp),
                vm = viewModel { MainScreenViewModel(DefaultDataRepository()) }
            )
        }
        entry<XFilterSelectorCameraScreenX> {
            XViewfinder(
                onItemClick = { navKey: NavKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp),
                vm = viewModel { MainScreenViewModel(DefaultDataRepository()) }
            )
        }
        entry<XViewCameraWithMedia3EffectScreenX> {
            XViewfinder(
                onItemClick = { navKey: NavKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp),
                vm = viewModel { MainScreenViewModel(DefaultDataRepository()) }
            )
        }
    })
}
