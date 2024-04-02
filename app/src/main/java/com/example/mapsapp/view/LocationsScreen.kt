package com.example.mapsapp.view

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.MyDrawer
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LocationsScreen(navigationController: NavController, myViewModel: MyViewModel) {
    MyDrawer(myViewModel, navigationController)
}

@Composable
fun Locations(navigationController: NavController, myViewModel: MyViewModel) {
    val markers by myViewModel.markersList.observeAsState()
    LazyColumn (modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)){
        items(markers!!.size) {
            MyRecyclerView(markers!![it], navigationController, myViewModel)
        }
    }
}

@Composable
fun MyRecyclerView(marca: Marca, navigationController: NavController, myViewModel: MyViewModel) {
    Card(
        border = BorderStroke(2.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { navigationController.navigate(Routes.DetailScreen.createRoute(marca.name)) }) {
            if (marca.photo != null) {
                Image(
                    bitmap = marca.photo.asImageBitmap(),
                    contentDescription = "Character Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.addimage),
                    contentDescription = "Add Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp).clickable {
                        myViewModel.changeNameMarker(marca.name)
                        myViewModel.changeTypeMarker(marca.tipo)
                        myViewModel.changePosMarker(marca.pos)
                        myViewModel.changeisAddImage()
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
                    fontFamily = sky
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Image(
                    painter = painterResource(id = myViewModel.whatIconBig(marca.tipo)), contentDescription = "Icono",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun MyDropDownMenu(myViewModel: MyViewModel): Int {
    var selectedImage by remember { mutableStateOf(R.drawable.all) }
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
                modifier = Modifier.size(50.dp)
                    .align(Alignment.Center)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp).background(color = Color.Black)
        ) {
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
    }
    val context = LocalContext.current
    val resourceName = getResourceNameFromContext(context, selectedImage)
    myViewModel.optionChoosed(resourceName!!)
    return selectedImage
}

fun getResourceNameFromContext(context: Context, resourceId: Int): String? {
    return try {
        context.resources.getResourceName(resourceId)?.split("/")?.lastOrNull()
    } catch (e: Resources.NotFoundException) {
        null
    }
}














