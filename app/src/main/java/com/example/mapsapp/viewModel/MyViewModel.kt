package com.example.mapsapp.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.google.android.gms.maps.model.LatLng

class MyViewModel: ViewModel() {
    private val _markers = MutableLiveData<List<Marca>>(emptyList())
    val markers = _markers
    private val _showBottomSheet = MutableLiveData<Boolean>(false)
    val showBottomSheet = _showBottomSheet
    private val _nameMarker = MutableLiveData<String>()
    val nameMaker = _nameMarker
    private val _posMarker = MutableLiveData<LatLng>()
    val posMarker = _posMarker
    private val _photoMaker = MutableLiveData<Bitmap?>()
    val photoMarker = _photoMaker
    private val _press = MutableLiveData<Boolean>(false)
    val press = _press
    private val _typeMarker = MutableLiveData<String>("avion")
    val typeMarker = _typeMarker
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted
    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale
    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied
    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation = _currentLocation
    private val _isCurrentLocation = MutableLiveData(false)
    val isCurrentLocation = _isCurrentLocation
    private val _isAddImage = MutableLiveData(false)
    val isAddImage = _isAddImage
    private val _listaIconos = MutableLiveData(listOf(R.drawable.all ,R.drawable.aviongrande, R.drawable.gasolineragrande, R.drawable.hospitalgrande, R.drawable.hotelgrande, R.drawable.restgrande, R.drawable.supergrande))
    val listaIconos = _listaIconos
    private val _markersList = MutableLiveData<List<Marca>>(emptyList())
    val markersList = _markersList
    val listaMarcadores = listOf(R.drawable.airport, R.drawable.fillingstation, R.drawable.hospital, R.drawable.hotel_0star, R.drawable.restaurant, R.drawable.supermarket)

    fun addMarker(){
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.add(Marca(_posMarker.value!!, _nameMarker.value!!, _typeMarker.value!!, _photoMaker.value))
        _markers.value = currentList
    }

    fun editImageMarker(posMarker:LatLng){
        val currentList = _markers.value.orEmpty().toMutableList()
        val index = currentList.indexOf(currentList.find { it.pos == posMarker })
        currentList[index] = Marca(posMarker, _nameMarker.value!!, _typeMarker.value!!, _photoMaker.value)
        _posMarker.value = null
        _nameMarker.value = ""
        _photoMaker.value = null
        _typeMarker.value = "avion"
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

    fun whatIconBig(tipo:String):Int {
        return when(tipo){
            "avion" -> listaIconos.value!![1]
            "gasolinera" -> listaIconos.value!![2]
            "hospital" -> listaIconos.value!![3]
            "hotel" -> listaIconos.value!![4]
            "restaurante" -> listaIconos.value!![5]
            "supermercado" -> listaIconos.value!![6]
            else -> 0
        }
    }

    fun changePhotoMarker(photo:Bitmap?){
        _photoMaker.value = photo
    }

    fun setCameraPermissionGranted(granted:Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should:Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied:Boolean) {
        _showPermissionDenied.value = denied
    }

    fun changeCurrentLocation(location:LatLng) {
        _currentLocation.value = location
    }

    fun changeisCurrentLocation(){
        _isCurrentLocation.value = !_isCurrentLocation.value!!
    }

    fun changeisAddImage(){
        _isAddImage.value = !_isAddImage.value!!
    }

    fun optionChoosed(option:String) {
        println(_markersList.value)
        var type : String = ""
        when (option) {
            "aviongrande" -> type = "avion"
            "gasolineragrande" -> type = "gasolinera"
            "hospitalgrande" -> type = "hospital"
            "hotelgrande" -> type = "hotel"
            "restgrande" -> type = "restaurante"
            "supergrande" -> type = "supermercado"
        }
        if (type != "") filterMarkers(type)
        else _markersList.value = _markers.value
    }

    fun filterMarkers(type:String){
        _markersList.value = _markers.value!!.filter { it.tipo == type }
    }
}