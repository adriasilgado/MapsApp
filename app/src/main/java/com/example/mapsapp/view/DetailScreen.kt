package com.example.mapsapp.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.mapsapp.viewModel.MyViewModel

@Composable
fun DetailScreen(name:String, navigationController: NavController, myViewModel: MyViewModel) {
    Text(text = name)
}