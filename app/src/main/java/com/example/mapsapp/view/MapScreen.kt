package com.example.mapsapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(state: DrawerState, navigationController: NavController, myViewModel: MyViewModel) {
    Scaffold(topBar = { MyTopAppBar(state) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Map(navigationController, myViewModel)
            BottomSheet(myViewModel)
        }
    }

}

@Composable
fun Map(navigationController: NavController, myViewModel: MyViewModel) {
    val markers by myViewModel.markers.observeAsState()
    val itb = LatLng(41.4534265, 2.1837151)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(itb, 14f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { longClickedLatLng ->
            myViewModel.addMarker(longClickedLatLng)
            myViewModel.changeBottomSheetState()
        }
    ) {
        markers!!.forEach { marker ->
            println("marca: $marker")
            Marker(
                state = MarkerState(position = marker),
                title = "ITB",
                snippet = "Marker at ITB"
                //icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(myViewModel: MyViewModel) {
    Column (
        modifier = Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        val show by myViewModel.showBottomSheet.observeAsState()
        val name by myViewModel.name.observeAsState("")
        if (show!!) {
            ModalBottomSheet(
                onDismissRequest = {
                    myViewModel.changeBottomSheetState()
                },
                sheetState = sheetState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Que nom vols posar-li?",
                    fontSize = 30.sp,
                    fontFamily = sky,
                    textAlign = TextAlign.Center)
                OutlinedTextField(
                    value = name!!,
                    onValueChange = { myViewModel.changeName(it)},
                    label = { Text("Nom Ubicació") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Green,
                        unfocusedBorderColor = Color.Black
                    ))
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            myViewModel.changeBottomSheetState()
                        }
                    }
                }) {
                    Text("Hide bottom sheet")
                }
            }
        }
    }
}


