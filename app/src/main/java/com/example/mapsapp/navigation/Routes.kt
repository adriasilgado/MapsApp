package com.example.mapsapp.navigation

sealed class Routes(val route:String) {
    object MapScreen: Routes("map_screen")
    object CameraScreen: Routes("camera_screen")
    object LocationsScreen: Routes("locations_screen")
    object DetailScreen:Routes("detail_screen/{name}") {
        fun createRoute(name: String) = "detail_screen/$name"
    }
    object GalleryScreen: Routes("gallery_screen")
    object LoginScreen: Routes("login_screen")
    object SignUpScreen: Routes("sign_up_screen")
}