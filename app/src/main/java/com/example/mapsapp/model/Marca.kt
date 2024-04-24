package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class Marca (
    var usuario:String? = null,
    var markerId:String? = null,
    val lat:Double,
    val lon:Double,
    var name:String,
    var tipo:String,
    var photo: String?
) {
    constructor():this("", "", 0.0, 0.0, "", "", "")
}