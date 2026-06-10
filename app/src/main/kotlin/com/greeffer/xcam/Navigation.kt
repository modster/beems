package com.greeffer.xcam

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.greeffer.xcam.data.XCameraFilterEntries
import com.greeffer.xcam.ui.main.MainScreen
import com.greeffer.xcam.ui.main.MainScreenViewModel
import com.greeffer.xcam.ui.main.XViewfinder

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainNavigation()
{
    val backStack = rememberNavBackStack(Main)

    NavDisplay(
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      entryProvider = entryProvider {
          entry<Main> {
              MainScreen(
                onItemClick = { navKey: NavKey -> backStack.add(navKey) },
                modifier = Modifier
                  .safeDrawingPadding()
                  .padding(16.dp),
              )
          }

          entry<XViewFinderX> {
              XViewfinder(
                vm = viewModel {
                    MainScreenViewModel(
                      XCameraFilterEntries(),
                    )
                },
              )
          }

          entry<XCamScreenX> {
              XViewfinder(
                vm = viewModel { MainScreenViewModel(XCameraFilterEntries()) },
              )
          }

          entry<XVignetterShaderProgramX> {
              XViewfinder(
                vm = viewModel { MainScreenViewModel(XCameraFilterEntries()) },
              )
          }

          entry<XFilterSelectorCameraScreenX> {
              XViewfinder(
                vm = viewModel { MainScreenViewModel(XCameraFilterEntries()) },
              )
          }
          entry<XViewCameraWithMedia3EffectScreenX> {
              XViewfinder(
                vm = viewModel { MainScreenViewModel(XCameraFilterEntries()) },
              )
          }
      },
    )
}
