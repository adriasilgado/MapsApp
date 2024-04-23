package com.example.mapsapp.view

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import java.io.OutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val context = LocalContext.current
    val isAddImage by myViewModel.isAddImage.observeAsState()
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }
    val img: Bitmap? = ContextCompat.getDrawable(context, R.drawable.addimage)?.toBitmap()
    var bitmap:Bitmap? by remember { mutableStateOf(null) }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if(bitmap == null) {
            CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
        }
        else {
            Image(
                bitmap = bitmap!!.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = { navigationController.navigateUp() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                if (bitmap == null) {
                    IconButton(
                        onClick = {
                            controller.cameraSelector =
                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                        },
                        modifier = Modifier
                            .offset(16.dp, 16.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera")
                    }
                    Button(
                        onClick = {
                            takePhoto(context, controller) { photo ->
                                myViewModel.changePhotoMarker(bitmapToUri(context, photo))
                                if (isAddImage == true) {
                                    myViewModel.changeisAddImage()
                                    myViewModel.editMarker(
                                        Marca(
                                            myViewModel.userId.value!!,
                                            myViewModel.markerId.value!!,
                                            myViewModel.posMarker.value!!.latitude,
                                            myViewModel.posMarker.value!!.longitude,
                                            myViewModel.nameMaker.value!!,
                                            myViewModel.typeMarker.value!!,
                                            myViewModel.photoMarker.value!!,
                                        )
                                    )
                                    myViewModel.changeNameMarker("")
                                    myViewModel.changeTypeMarker("")
                                    val pos: LatLng = LatLng(0.0, 0.0)
                                    myViewModel.changePosMarker(pos)
                                    myViewModel.changeisAddImage()
                                    myViewModel.changeMarkerId("")
                                    myViewModel.changePhotoMarker(null)
                                    navigationController.navigate(Routes.LocationsScreen.route)
                                }
                                navigationController.navigateUp()
                            }
                        },
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp)
                            .border(7.dp, Color(230, 222, 221), shape = CircleShape),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            Color.White
                        )
                    ) {

                    }
                }
                else {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp)
                            .border(7.dp, Color(230, 222, 221), shape = CircleShape),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Check", tint = Color.Black)
                    }
                }
                IconButton(onClick = {
                    launchImage.launch("image/*")},
                    modifier = Modifier
                        .offset(16.dp, 16.dp)
                        .padding(end = 16.dp)) {
                    Icon(imageVector = Icons.Default.Photo, contentDescription = "Gallery")
                }
            }
        }
    }
}

@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = modifier
    )
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val matri = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matri,
                    true
                )
                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Error taking photo", exception)
            }
        }
    )
}

fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
    val filename = "${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, filename)
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    }

    val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    uri?.let {
        val outstream: OutputStream? = context.contentResolver.openOutputStream(it)
        outstream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        outstream?.close()
    }
    return uri
}