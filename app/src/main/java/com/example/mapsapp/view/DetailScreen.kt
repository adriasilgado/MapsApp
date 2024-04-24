package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(markerId:String, navigationController: NavController, myViewModel: MyViewModel) {
    myViewModel.getMarker(markerId)

    if (myViewModel.posMarker.value != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { navigationController.navigate(Routes.LocationsScreen.route) },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back"
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(shape = RoundedCornerShape(4.dp), elevation = cardElevation(4.dp)) {
                    Text(
                        text = "Marker Name: ${myViewModel.nameMaker.value}",
                        fontFamily = sky,
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Card(shape = RoundedCornerShape(4.dp), elevation = cardElevation(4.dp)) {
                    Text(
                        text = "Marker Type: ${myViewModel.typeMarker.value}",
                        fontFamily = sky,
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Card(shape = RoundedCornerShape(4.dp), elevation = cardElevation(4.dp)) {
                    DetailMap(Marca(
                        myViewModel.userId.value,
                        myViewModel.markerId.value,
                        myViewModel.posMarker.value!!.latitude,
                        myViewModel.posMarker.value!!.longitude,
                        myViewModel.nameMaker.value!!,
                        myViewModel.typeMarker.value!!,
                        myViewModel.photoMarker.value
                    ), myViewModel)
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (myViewModel.photoMarker.value != null) {
                    Card(shape = RoundedCornerShape(4.dp), elevation = cardElevation(4.dp)) {
                        GlideImage(
                            model = myViewModel.photoMarker.value!!,
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(4.dp))
                                .background(Color.DarkGray)
                                .padding(8.dp)
                        )
                    }
                } else {
                    Card(shape = RoundedCornerShape(4.dp), elevation = cardElevation(4.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.addimage),
                            contentDescription = "Add Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    myViewModel.changeMarkerId(markerId)
                                    myViewModel.setIsAddImage(true)
                                    navigationController.navigate(Routes.CameraScreen.route)
                                }
                                .background(Color.DarkGray)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailMap(marker: Marca, myViewModel: MyViewModel) {
    val position = LatLng(marker.lat, marker.lon)
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(25.dp)),
        cameraPositionState = CameraPositionState(CameraPosition(position, 16f, 0f, 0f)),
        uiSettings = MapUiSettings(
            compassEnabled = false,
            tiltGesturesEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false,
            scrollGesturesEnabled = false
        ),
        properties = MapProperties(
            isBuildingEnabled = true,
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false,
        )
    ) {
        val pos:LatLng = LatLng(marker.lat, marker.lon)
        Marker(
            state = MarkerState(position = pos),
            title = marker.name,
            icon = BitmapDescriptorFactory.fromResource(myViewModel.whatIcon(marker.tipo))
        )
    }
}

