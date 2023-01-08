package company.vk.education.siriusapp.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import company.vk.education.siriusapp.ui.screens.Screens
import company.vk.education.siriusapp.ui.screens.main.MainScreenDeps
import company.vk.education.siriusapp.ui.screens.main.mainScreen
import company.vk.education.siriusapp.ui.screens.trip.TripScreenDeps
import company.vk.education.siriusapp.ui.screens.trip.tripScreen
import company.vk.education.siriusapp.ui.screens.user.UserScreenDeps
import company.vk.education.siriusapp.ui.screens.user.userScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var godOfMappers: GodOfMappers
    @Inject lateinit var locationPermissionHandler: LocationPermissionHandler

    private val tripScreenDeps by lazy { TripScreenDeps(godOfMappers) }
    private val userScreenDeps by lazy { UserScreenDeps(godOfMappers) }
    private val mainScreenDeps by lazy {
        MainScreenDeps(godOfMappers, locationPermissionHandler)
    }

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionHandler.init()
        setContent {
            val sheetState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )
            val bottomSheetNavigator = remember { BottomSheetNavigator(sheetState) }
            val navController = rememberNavController(bottomSheetNavigator)

            ModalBottomSheetLayout(bottomSheetNavigator) {
                NavHost(
                    navController = navController,
                    startDestination = Screens.Main.route
                ) {
                    mainScreen(navController, mainScreenDeps)
                    tripScreen(tripScreenDeps)
                    userScreen(navController, userScreenDeps)
                }
            }
        }
    }
}

