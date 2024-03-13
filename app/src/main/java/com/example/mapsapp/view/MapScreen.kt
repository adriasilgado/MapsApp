package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.MainActivity
import com.example.mapsapp.R
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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
fun MapScreen(state: DrawerState, navigationController: NavController, myViewModel: MyViewModel) {
    Scaffold(topBar = { MyTopAppBar(state) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Map(navigationController, myViewModel)
            BottomSheet(navigationController, myViewModel)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("MissingPermission")
@Composable
fun Map(navigationController: NavController, myViewModel: MyViewModel) {
    val markers by myViewModel.markers.observeAsState()
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
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isBuildingEnabled = true, mapType = MapType.NORMAL, isMyLocationEnabled = true),
        onMapLongClick = { longClickedLatLng ->
            myViewModel.changePosMarker(longClickedLatLng)
            myViewModel.changeBottomSheetState()
        }
    ) {
        markers!!.forEach { marker ->
            println("marca: $marker")
            Marker(
                state = MarkerState(position = marker.pos),
                title = marker.name,
                icon = BitmapDescriptorFactory.fromResource(myViewModel.whatIcon(marker.tipo))
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(state: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BottomSheet(navigationController: NavController, myViewModel: MyViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val show by myViewModel.showBottomSheet.observeAsState()
    val name by myViewModel.nameMaker.observeAsState("")
    val press by myViewModel.press.observeAsState()
    if (show!!) {
        ModalBottomSheet(
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
                label = { Text("Enter marker name") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.Black
                ))
            Spacer(modifier = Modifier.height(20.dp))
            EleccionTipo(myViewModel)
            Spacer(modifier = Modifier.height(20.dp))
            IconButton(onClick = {
                navigationController.navigate(Routes.CameraScreen.route) }) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row (horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp)){
                Button(onClick = {
                    myViewModel.changeNameMarker("")
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
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(25.dp))
                Button(onClick = {
                    myViewModel.addMarker()
                    myViewModel.changeNameMarker("")
                    myViewModel.changeTypeMarker("avion")
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
                    colors = ButtonDefaults.buttonColors(Color.Black))
                    {
                    Text("Add marker")
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
        for (i in 0 until myViewModel.listaIconos.size) {
            Box (modifier = Modifier
                .aspectRatio(1f)
                .weight(1f))
                    {
                IconButton(modifier = Modifier
                    .fillMaxSize()
                    .border(
                        BorderStroke(
                            width = 2.dp,
                            color = if (typeMarker == iconoSelect(i)) Color.Green else Color.Transparent
                        ),
                        shape = CircleShape
                    ), onClick = { myViewModel.changeTypeMarker(iconoSelect(i))}) {
                    Image(painter = painterResource(id = myViewModel.listaIconos[i]), contentDescription = "icon")
                }
            }
        }
    }
}

fun iconoSelect(iterador:Int):String {
    when (iterador) {
        0 -> return "avion"
        1 -> return "gasolinera"
        2 -> return "hospital"
        3 -> return "hotel"
        4 -> return "restaurante"
        5 -> return "supermercado"
        else -> return ""
    }
}


