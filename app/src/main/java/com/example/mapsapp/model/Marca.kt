package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class Marca (
    val pos:LatLng,
    val name:String,
    val tipo:String,
    val photo: Bitmap?
)