package com.example.mapsapp.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel

@Composable
fun LocationsScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val markers by myViewModel.markers.observeAsState()
    Column (modifier = Modifier.fillMaxWidth().padding(10.dp)){
        markers!!.forEach() {marca ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
                .height(50.dp)
                .clickable { navigationController.navigate(Routes.DetailScreen.createRoute(marca.name)) }) {
                Text(text = marca.name, modifier = Modifier.align(Alignment.Center),textAlign = TextAlign.Center, fontFamily = sky, color = Color.Black)
            }
        }
    }

}