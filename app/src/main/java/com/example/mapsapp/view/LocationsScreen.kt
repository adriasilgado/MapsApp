package com.example.mapsapp.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.model.Marca
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel

@Composable
fun LocationsScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val markers by myViewModel.markers.observeAsState()
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
            Image(
                bitmap = marca.photo!!.asImageBitmap(),
                contentDescription = "Character Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )
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