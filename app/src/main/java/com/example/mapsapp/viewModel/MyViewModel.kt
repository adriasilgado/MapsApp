package com.example.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.R
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
    private val _typeMarker = MutableLiveData<String>("avion")
    val typeMarker = _typeMarker
    private val _cameraPress = MutableLiveData<Boolean>(false)
    val cameraPress = _cameraPress
    val listaIconos = listOf(R.drawable.aviongrande, R.drawable.gasolineragrande, R.drawable.hospitalgrande, R.drawable.hotelgrande, R.drawable.restgrande, R.drawable.supergrande)
    val listaMarcadores = listOf(R.drawable.airport, R.drawable.fillingstation, R.drawable.hospital, R.drawable.hotel_0star, R.drawable.restaurant, R.drawable.supermarket)

    fun addMarker(){
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.add(Marca(_posMarker.value!!, _nameMarker.value!!, _typeMarker.value!!))
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

    fun changeTypeMarker(type:String){
        _typeMarker.value = type
    }

    fun whatIcon(tipo:String):Int {
        return when(tipo){
            "avion" -> listaMarcadores[0]
            "gasolinera" -> listaMarcadores[1]
            "hospital" -> listaMarcadores[2]
            "hotel" -> listaMarcadores[3]
            "restaurante" -> listaMarcadores[4]
            "supermercado" -> listaMarcadores[5]
            else -> 0
        }
    }

    fun changeCameraPress(){
        _cameraPress.value = true
    }
}