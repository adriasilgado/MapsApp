package com.example.mapsapp.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel

@Composable
fun GalleryScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val context = LocalContext.current
    val img: Bitmap? = ContextCompat.getDrawable(context, R.drawable.addimage)?.toBitmap()
    var bitmap:Bitmap? by remember { mutableStateOf(null) }
    val isAddImage by myViewModel.isAddImage.observeAsState()
    var uri: Uri? by remember { mutableStateOf(null) }
    val launchImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = it?.let { it1 ->
                    ImageDecoder.createSource(context.contentResolver, it1)
                }
                source?.let { it1 -> ImageDecoder.decodeBitmap(it1)
                }
            }
            uri = it
        })
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navigationController.navigateUp() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(onClick = {

            },
                modifier = Modifier
                    .fillMaxHeight(0.05f)
                    .width(150.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(Color.DarkGray)) {
                Text("Upload Storage", fontFamily = sky)
            }
            Button(onClick = {
                launchImage.launch("image/*")
            },
                modifier = Modifier
                    .fillMaxHeight(0.05f)
                    .width(150.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(Color.DarkGray)) {
                Text("Open Gallery", fontFamily = sky)
            }
            Image(
                bitmap = if (bitmap != null) bitmap!!.asImageBitmap() else img!!.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.Crop, modifier = Modifier
                    .clip(CircleShape)
                    .size(250.dp)
                    .background(Color.Transparent)
                    .border(width = 1.dp, color = Color.White, shape = CircleShape)
            )
            Button(onClick = {
                println(uri)
                if (uri != null) myViewModel.uploadImage(uri!!)
                myViewModel.changePhotoMarker(uri)
                if (isAddImage == true) {
                    myViewModel.editMarker(Marca(
                        myViewModel.markerId.value!!,
                        myViewModel.currentLocation.value!!.latitude,
                        myViewModel.currentLocation.value!!.longitude,
                        myViewModel.nameMaker.value!!,
                        myViewModel.typeMarker.value!!,
                        myViewModel.photoMarker.value!!,
                        ))
                    myViewModel.changeisAddImage()
                    navigationController.navigate(Routes.LocationsScreen.route)
                }
                else navigationController.navigate(Routes.MapScreen.route)
            },
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .width(150.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                enabled = bitmap != null) {
                Text("Save", fontFamily = sky)
            }
        }
    }

}
