package com.example.mapsapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.CameraScreen
import com.example.mapsapp.view.EstructuraMap
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

val sky = FontFamily(Font(R.font.skyland))

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
                        startDestination = Routes.MapScreen.route) {
                        composable(Routes.MapScreen.route) { MapScreen(navigationController, myViewModel) }
                        composable(Routes.CameraScreen.route) { CameraScreen(navigationController, myViewModel) }}
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
    val markers by myViewModel.markers.observeAsState()
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false ,drawerContent = {
    ModalDrawerSheet {
        IconButton(onClick = { scope.launch {
            state.close()
        } }) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
        }
        Divider()
        markers!!.forEach { marker ->
            NavigationDrawerItem(label = {Text(marker.name)}, selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    //navegar
                })
        }
    } }) {
        val permissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        if (permissionState.status.isGranted) {
            EstructuraMap(state, navigationController, myViewModel)
        }
        else {
            Text("Need permission")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MapsAppTheme {
    }
}