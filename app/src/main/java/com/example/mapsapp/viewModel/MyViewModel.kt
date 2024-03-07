package com.example.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MyViewModel: ViewModel() {
    private val _markers = MutableLiveData<List<LatLng>>(emptyList())
    val markers = _markers

    fun addMarker(pos:LatLng){
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.add(pos)
        _markers.value = currentList
    }
}