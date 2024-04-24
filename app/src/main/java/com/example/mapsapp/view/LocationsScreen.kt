package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.MyDrawer
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LocationsScreen(navigationController: NavController, myViewModel: MyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        MyDrawer(myViewModel, navigationController)
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun Locations(navigationController: NavController, myViewModel: MyViewModel) {
    myViewModel.changeNameMarker("")
    myViewModel.changeTypeMarker("")
    val pos: LatLng = LatLng(0.0, 0.0)
    myViewModel.changePosMarker(pos)
    myViewModel.changeMarkerId("")
    myViewModel.changePhotoMarker(null)
    val markers by myViewModel.markersList.observeAsState()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            items(markers!!.size) {index ->
                MarcaItem(markers!![index], onRemove = { myViewModel.deleteMarker(markers!![index].markerId!!) }, navigationController, myViewModel)
            }
        }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MyRecyclerView(marca: Marca, navigationController: NavController, myViewModel: MyViewModel) {
    Card(
        border = BorderStroke(2.dp, Color.DarkGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        )
    ) {
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { navigationController.navigate(Routes.DetailScreen.createRoute(marca.markerId!!)) }) {
            if (marca.photo != null) {
                GlideImage(
                    model = marca.photo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(125.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.addimage),
                    contentDescription = "Add Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            println("la id es ${marca.markerId}, el marker es $marca")
                            myViewModel.setMarker(Marca(
                                usuario = marca.usuario,
                                markerId = marca.markerId,
                                lat = marca.lat,
                                lon = marca.lon,
                                name = marca.name,
                                tipo = marca.tipo,
                                photo = null
                            ))
                            myViewModel.changeMarkerId(marca.markerId!!)
                            myViewModel.setIsAddImage(true)
                            navigationController.navigate(Routes.CameraScreen.route)
                        }
                )
            }
            Column {
                Text(
                    text = marca.name,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize(),
                    fontFamily = sky,
                    color = Color.White
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Image(
                    painter = painterResource(id = myViewModel.whatIconBig(marca.tipo)),
                    contentDescription = "Icono",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun MyDropDownMenu(myViewModel: MyViewModel, edit:Boolean): Int {
    var selectedImage by remember { mutableStateOf(if (edit) R.drawable.aviongrande else R.drawable.all) }
    var expanded by remember { mutableStateOf(false) }
    val images by myViewModel.listaIconos.observeAsState()

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .width(150.dp)
            .wrapContentHeight()
            .border(
                width = 3.dp,
                color = Color.Transparent
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clickable { expanded = true }
                .fillMaxWidth()
                .width(150.dp)
                .background(color = Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = selectedImage),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(150.dp)
                .background(color = Color.Black)
        ) {
            if (!edit) {
                images!!.forEach { image ->
                    Box(
                        modifier = Modifier
                            .clickable {
                                expanded = false
                                selectedImage = image
                            }
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
            else {
                for (i in 1 until images!!.size) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                expanded = false
                                selectedImage = images!![i]
                            }
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = images!![i]),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    }
    if (!edit) {
        val context = LocalContext.current
        val resourceName = getResourceNameFromContext(context, selectedImage)
        myViewModel.optionChoosed(resourceName!!)
    }
    println(selectedImage)
    return selectedImage
}

fun getResourceNameFromContext(context: Context, resourceId: Int): String? {
    return try {
        context.resources.getResourceName(resourceId)?.split("/")?.lastOrNull()
    } catch (e: Resources.NotFoundException) {
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.EndToStart -> Color.Red
        else -> Color.Transparent
    }
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (direction == DismissDirection.EndToStart) {
            Icon(Icons.Default.Delete, contentDescription = "delete")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcaItem(marca: Marca, onRemove: (Marca) -> Unit, navigationController: NavController, myViewModel: MyViewModel) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(marca)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                show = false
                true
            } else false
        }, positionalThreshold = { 125.dp.toPx() }
    )
    AnimatedVisibility(
        show, exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            modifier = Modifier,
            background = { DismissBackground(dismissState) },
            dismissContent = {
                MyRecyclerView(marca, navigationController, myViewModel)
            })
    }
    LaunchedEffect(show) {
        if (!show) {
            delay(800)
            onRemove(currentItem)
            Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
        }
    }
}












