package com.example.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MyViewModel: ViewModel() {
    private val _markers = MutableLiveData<List<LatLng>>(emptyList())
    val markers = _markers
    private val _showBottomSheet = MutableLiveData<Boolean>(false)
    val showBottomSheet = _showBottomSheet
    private val _name = MutableLiveData<String>()
    val name = _name

    fun addMarker(pos:LatLng){
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.add(pos)
        _markers.value = currentList
    }

    fun changeBottomSheetState(){
        _showBottomSheet.value = !_showBottomSheet.value!!
    }

    fun changeName(name: String){
        _name.value = name
    }
}