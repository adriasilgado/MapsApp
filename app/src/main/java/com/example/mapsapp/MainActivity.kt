package com.example.mapsapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mapsapp.data.UserPrefs
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.CameraScreen
import com.example.mapsapp.view.DetailScreen
import com.example.mapsapp.view.GalleryScreen
import com.example.mapsapp.view.MyScaffold
import com.example.mapsapp.view.LocationsScreen
import com.example.mapsapp.view.LoginScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.SignUpScreen
import com.example.mapsapp.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

val sky = FontFamily(Font(R.font.bloom))

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val myViewModel by viewModels<MyViewModel>()
        super.onCreate(savedInstanceState)
        setContent {
            MapsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController = rememberNavController()
                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.LoginScreen.route) {
                        composable(Routes.MapScreen.route) { MapScreen(navigationController, myViewModel) }
                        composable(Routes.CameraScreen.route) { CameraScreen(navigationController, myViewModel) }
                        composable(Routes.LocationsScreen.route) { LocationsScreen(navigationController, myViewModel) }
                        composable(
                            Routes.DetailScreen.route,
                            arguments = listOf(
                                navArgument("name") {type = NavType.StringType})) {
                                backStackEntry ->
                            DetailScreen(
                                backStackEntry.arguments?.getString("name").orEmpty(),
                                navigationController, myViewModel
                            )}
                        composable(Routes.GalleryScreen.route) { GalleryScreen(navigationController, myViewModel) }
                        composable(Routes.LoginScreen.route) { LoginScreen(navigationController, myViewModel) }
                        composable(Routes.SignUpScreen.route) { SignUpScreen(navigationController, myViewModel) }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyDrawer(myViewModel: MyViewModel, navigationController: NavController) {
    val scope = rememberCoroutineScope()
    val state:DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val userPrefs = remember { UserPrefs(context) }
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false ,drawerContent = {
    ModalDrawerSheet {
        IconButton(onClick = { scope.launch {
            state.close()
        } }) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
        }
        Divider()
        NavigationDrawerItem(label = {Text("Locations", fontFamily = sky)}, selected = false,
            onClick = {
                scope.launch {
                    state.close()
                }
                navigationController.navigate(Routes.LocationsScreen.route)
            })
        NavigationDrawerItem(label = {Text("Map", fontFamily = sky)}, selected = false,
            onClick = {
                scope.launch {
                    state.close()
                }
                if (currentRoute != "map_screen") {
                    navigationController.navigate(Routes.MapScreen.route)
                }
            })
        if (currentRoute == "map_screen") {
            NavigationDrawerItem(label = {Text("Add Marker", fontFamily = sky)}, selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    myViewModel.changeisCurrentLocation()
                    myViewModel.changeBottomSheetState()
                })
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            myViewModel.logout()
            scope.launch {
                userPrefs.saveUserData("", "", "")
            }
            myViewModel.setRememberMe(false)
            navigationController.navigate(Routes.LoginScreen.route)
        }) {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Red, modifier = Modifier.fillMaxSize(1f))
            }
            Text("Logout", fontFamily = sky, color = Color.Red, fontSize = 25.sp)
        }
    } }) {
        val permissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        if (permissionState.status.isGranted) {
            MyScaffold(state, navigationController, myViewModel)
        }
        else {
            Text("Need permission")
        }
    }
}
