package com.example.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.model.Marca
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MyViewModel: ViewModel() {
    private val _markers = MutableLiveData<List<Marca>>(emptyList())
    val markers = _markers
    private val _showBottomSheet = MutableLiveData<Boolean>(false)
    val showBottomSheet = _showBottomSheet
    private val _nameMarker = MutableLiveData<String>()
    val nameMaker = _nameMarker
    private val _posMarker = MutableLiveData<LatLng>()
    val posMarker = _posMarker
    private val _press = MutableLiveData<Boolean>(false)
    val press = _press

    fun addMarker(){
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.add(Marca(_posMarker.value!!, _nameMarker.value!!))
        _markers.value = currentList
    }

    fun changeBottomSheetState(){
        _showBottomSheet.value = !_showBottomSheet.value!!
    }

    fun changeNameMarker(name:String){
        _nameMarker.value = name
    }

    fun changePosMarker(pos:LatLng){
        _posMarker.value = pos
    }

    fun changePress(press:Boolean){
        _press.value = press
    }
}