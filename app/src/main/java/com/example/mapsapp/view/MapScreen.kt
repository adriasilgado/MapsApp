package com.example.mapsapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.viewModel.MyViewModel
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
            myViewModel.changePosMarker(longClickedLatLng)
            myViewModel.changeBottomSheetState()
        }
    ) {
        markers!!.forEach { marker ->
            println("marca: $marker")
            Marker(
                state = MarkerState(position = marker.pos),
                title = marker.name,
                snippet = "Marker at ITB"
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
fun BottomSheet( myViewModel: MyViewModel) {
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
            eleccionTipo(myViewModel)
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
                    shape = RoundedCornerShape(25.dp)) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(25.dp))
                Button(onClick = {
                    myViewModel.addMarker()
                    myViewModel.changeNameMarker("")
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
                    shape = RoundedCornerShape(25.dp)) {
                    Text("Add marker")
                }
            }

        }
    }
}

@Composable
fun eleccionTipo(myViewModel: MyViewModel) {
    val typeMarker by myViewModel.typeMarker.observeAsState()
    Row {
        Row (verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.4f)){
            RadioButton(
                selected = typeMarker == "RESTAURANTE",
                onClick = { myViewModel.changeTypeMarker("RESTAURANTE") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(73, 235, 75),
                    unselectedColor = Color(210, 51, 36 )
                )
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.6f).padding(start = 10.dp)){
            RadioButton(
                selected = typeMarker == "SUPERMERCADO",
                onClick = { myViewModel.changeTypeMarker("SUPERMERCADO") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Green,
                    unselectedColor = Color.Red
                )
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.3f)){
            RadioButton(
                selected = typeMarker == "HOTEL",
                onClick = { myViewModel.changeTypeMarker("HOTEL") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Green,
                    unselectedColor = Color.Red
                )
            )
        }
    }
}


