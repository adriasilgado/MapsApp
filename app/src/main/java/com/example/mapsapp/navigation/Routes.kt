package com.example.mapsapp.navigation

sealed class Routes(val route:String) {
    object MapScreen: Routes("map_screen")
}