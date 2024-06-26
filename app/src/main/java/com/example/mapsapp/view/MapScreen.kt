package com.example.mapsapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.MainActivity
import com.example.mapsapp.MyDrawer
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MapScreen(navigationController: NavController, myViewModel: MyViewModel) {
    MyDrawer(myViewModel, navigationController)
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MyScaffold(state: DrawerState, navigationController: NavController, myViewModel: MyViewModel) {
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val welcome by myViewModel.welcome.observeAsState()
    Scaffold(topBar = { MyTopAppBar(state, navigationController, myViewModel) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentRoute == "map_screen") {
                var context = LocalContext.current
                if (!welcome!!) {
                    myViewModel.setWelcome(true)
                    Toast.makeText(context, "Welcome, ${myViewModel.loggedUser.value}", Toast.LENGTH_SHORT).show()
                }
                Map(navigationController, myViewModel)
                BottomSheet(navigationController, myViewModel)
            }
            else if (currentRoute == "locations_screen") {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ){
                    Locations(navigationController, myViewModel)
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("MissingPermission")
@Composable
fun Map(navigationController: NavController, myViewModel: MyViewModel) {
    val markers by myViewModel.markers.observeAsState()
    myViewModel.getMarkers()
    val context = LocalContext.current
    val fusedLocationProveidorClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val cameraPositionState = rememberCameraPositionState{CameraPosition.fromLatLngZoom(deviceLatLng, 18f)}
    val locationResult = fusedLocationProveidorClient.getCurrentLocation(100, null)

    locationResult.addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            lastKnownLocation = task.result
            deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
        }
    }
    if (lastKnownLocation != null) {
        myViewModel.changeCurrentLocation(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude))
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isBuildingEnabled = true, mapType = MapType.NORMAL, isMyLocationEnabled = true),
        onMapLongClick = { longClickedLatLng ->
            myViewModel.changePosMarker(longClickedLatLng)
            myViewModel.changeBottomSheetState()
        }
    ) {
        if (markers != null) {
            markers!!.forEach { marker ->
                Log.i("IMAGEN MARKER", "Icono: ${marker.tipo}")
                val pos:LatLng = LatLng(marker.lat, marker.lon)
                Marker(
                    state = MarkerState(position = pos),
                    title = marker.name,
                    icon = BitmapDescriptorFactory.fromResource(myViewModel.whatIcon(marker.tipo))
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(state: DrawerState, navigationController: NavController, myViewModel: MyViewModel) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        actions = { if (currentRoute == "locations_screen") MyDropDownMenu(myViewModel, false)},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun BottomSheet(navigationController: NavController, myViewModel: MyViewModel) {
    val sheetState = rememberModalBottomSheetState(true)
    val scope = rememberCoroutineScope()
    val show by myViewModel.showBottomSheet.observeAsState()
    val name by myViewModel.nameMaker.observeAsState("")
    val press by myViewModel.press.observeAsState()
    val photoMarker by myViewModel.photoMarker.observeAsState()
    val context = LocalContext.current
    val isCameraPermissionGranted by myViewModel.cameraPermissionGranted.observeAsState(false)
    val shouldShowPermissionRationale by myViewModel.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by myViewModel.showPermissionDenied.observeAsState(false)
    val isCurrentLocation by myViewModel.isCurrentLocation.observeAsState(false)
    val uri by myViewModel.uri.observeAsState()
    println("imagen: $photoMarker")
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {isGranted ->
            if (isGranted) {
                myViewModel.setCameraPermissionGranted(true)
                navigationController.navigate(Routes.CameraScreen.route)
            }
            else {
                myViewModel.setShouldShowPermissionRationale(
                    shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                )
                if (!shouldShowPermissionRationale) {
                    Log.i("CameraScreen", "No podemos volver a pedir permisos")
                    myViewModel.setShowPermissionDenied(true)
                }
            }
        })
    if (show!!) {
        if (isCurrentLocation) {
            myViewModel.changePosMarker(myViewModel.currentLocation.value!!)
            myViewModel.changeisCurrentLocation()
        }
        ModalBottomSheet(
            containerColor = Color.Black,
            onDismissRequest = {
                myViewModel.changeBottomSheetState()
            },
            sheetState = sheetState
        ) {
            OutlinedTextField(
                value = name!!,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onValueChange = {
                    if (it.isEmpty()) myViewModel.changePress(false)
                    else myViewModel.changePress(true)
                    myViewModel.changeNameMarker(it)},
                label = { Text("Enter marker name", fontFamily = sky) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Black
                ))
            Spacer(modifier = Modifier.height(20.dp))
            EleccionTipo(myViewModel)
            Spacer(modifier = Modifier.height(20.dp))
            if (uri != null) {
                GlideImage(
                    model = uri!!,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp)
                )
            }
            else {
                IconButton(onClick = {
                    if (!isCameraPermissionGranted) {
                        launcher.launch(Manifest.permission.CAMERA)
                    }
                    else {
                        navigationController.navigate(Routes.CameraScreen.route)
                    }},
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Go to Camera",
                        modifier = Modifier.fillMaxSize(), tint = Color.White)
                }
                if (showPermissionDenied) {
                    PermissionDeclinedScreen()
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row (horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp)){
                Button(onClick = {
                    myViewModel.changeNameMarker("")
                    myViewModel.changePhotoMarker(null)
                    myViewModel.changeTypeMarker("avion")
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            myViewModel.changeBottomSheetState()
                        }
                    }
                },
                    modifier = Modifier
                        .fillMaxHeight(0.1f)
                        .width(150.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(Color.DarkGray)) {
                    Text("Cancel", fontFamily = sky, color = Color.White)
                }
                Spacer(modifier = Modifier.width(25.dp))
                Button(onClick = {
                    println("URI: $uri")
                    if (uri != null) {
                        myViewModel.uploadImage(
                            uri!!, Marca(
                                myViewModel.userId.value!!,
                                null,
                                myViewModel.posMarker.value!!.latitude,
                                myViewModel.posMarker.value!!.longitude,
                                myViewModel.nameMaker.value!!,
                                myViewModel.typeMarker.value!!,
                                myViewModel.photoMarker.value
                            )
                        )
                    }
                    else {
                        myViewModel.addMarker(
                            Marca(
                                myViewModel.userId.value!!,
                                null,
                                myViewModel.posMarker.value!!.latitude,
                                myViewModel.posMarker.value!!.longitude,
                                myViewModel.nameMaker.value!!,
                                myViewModel.typeMarker.value!!,
                                null
                            )
                        )
                        myViewModel.changeNameMarker("")
                        myViewModel.changePhotoMarker(null)
                        myViewModel.changeTypeMarker("avion")
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            myViewModel.changeBottomSheetState()
                        }
                    }
                },
                    enabled = press!!,
                    modifier = Modifier
                        .fillMaxHeight(0.1f)
                        .width(150.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(Color.DarkGray))
                    {
                    Text("Add marker", fontFamily = sky, color = Color.White)
                }
            }

        }
    }
}

@Composable
fun EleccionTipo(myViewModel: MyViewModel) {
    val typeMarker by myViewModel.typeMarker.observeAsState()
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)){
        for (i in 1 until myViewModel.listaIconos.value!!.size) {
            Box (modifier = Modifier
                .aspectRatio(1f)
                .weight(1f))
                    {
                IconButton(modifier = Modifier
                    .fillMaxSize()
                    .border(
                        BorderStroke(
                            width = 2.dp,
                            color = if (typeMarker == iconoSelect(i)) Color.White else Color.Transparent
                        ),
                        shape = CircleShape
                    ), onClick = { myViewModel.changeTypeMarker(iconoSelect(i))}) {
                    Image(painter = painterResource(id = myViewModel.listaIconos.value!![i]), contentDescription = "icon")
                }
            }
        }
    }
}

fun iconoSelect(iterador:Int):String {
    when (iterador) {
        1 -> return "avion"
        2 -> return "gasolinera"
        3 -> return "hospital"
        4 -> return "hotel"
        5 -> return "restaurante"
        6 -> return "supermercado"
        else -> return ""
    }
}

@Composable
fun PermissionDeclinedScreen() {
    val context = LocalContext.current
    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Permission required", fontWeight = FontWeight.Bold)
        Text(text = "This app needs acces to the camera to take photos")
        Button(onClick = {
            openAppSettings(context as Activity)
        }) {
            Text(text = "Accept")
        }
    }
}

fun openAppSettings(activity: Activity) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", activity.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    activity.startActivity(intent)
}


