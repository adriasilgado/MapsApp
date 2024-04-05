package com.example.mapsapp.model

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Repository {
    private val database = FirebaseFirestore.getInstance()

    fun addMarker(marker: Marca) {
        database.collection("Markers").add(
            hashMapOf(
                "lat" to marker.lat,
                "lon" to marker.lon,
                "name" to marker.name,
                "type" to marker.tipo,
                "photo" to marker.photo
            )
        )
    }

    fun editMarker(editMarker: Marca) {
        database.collection("Markers").document(editMarker.markerId!!).set(
            hashMapOf(
                "lat" to editMarker.lat,
                "lon" to editMarker.lon,
                "name" to editMarker.name,
                "type" to editMarker.tipo,
                "photo" to editMarker.photo
            )
        )
    }

    fun deleteMarker(markerId:String) {
        database.collection("Markers").document(markerId).delete()
    }

    fun getMarkers():CollectionReference {
        return database.collection("Markers")
    }

    fun getMarker(markerId:String):DocumentReference {
        return database.collection("Markers").document(markerId)
    }
}