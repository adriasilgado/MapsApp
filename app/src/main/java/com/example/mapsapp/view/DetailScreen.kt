package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(markerId:String, navigationController: NavController, myViewModel: MyViewModel) {
    myViewModel.getMarker(markerId)

    if (myViewModel.posMarker.value != null) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text(text = "Marker Name: ${myViewModel.nameMaker.value}", fontFamily = sky, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Marker Type: ${myViewModel.typeMarker.value}", fontFamily = sky, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Coordinates:\n ${myViewModel.posMarker.value}",
                fontFamily = sky, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))


            if (myViewModel.photoMarker.value != null) {
                GlideImage(
                    model = myViewModel.photoMarker.value!!,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(4.dp))
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.addimage),
                    contentDescription = "Add Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { navigationController.navigate(Routes.CameraScreen.route) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navigationController.navigateUp() }) {
                Text(text = "Back")
            }
        }
    }
}