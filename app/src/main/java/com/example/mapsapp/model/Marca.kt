package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class Marca (
    var usuario:String? = null,
    var markerId:String? = null,
    val lat:Double,
    val lon:Double,
    val name:String,
    val tipo:String,
    val photo: String?
) {
    constructor():this("", "", 0.0, 0.0, "", "", "")
}