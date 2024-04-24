package com.example.mapsapp.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.R
import com.example.mapsapp.model.Marca
import com.example.mapsapp.model.Repository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.storage.FirebaseStorage
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyViewModel: ViewModel() {
    private val repository = Repository()
    private val _markers = MutableLiveData<List<Marca>>(emptyList())
    val markers = _markers
    private val _showBottomSheet = MutableLiveData<Boolean>(false)
    val showBottomSheet = _showBottomSheet
    private val _nameMarker = MutableLiveData<String>()
    val nameMaker = _nameMarker
    private val _posMarker = MutableLiveData<LatLng>()
    val posMarker = _posMarker
    private val _photoMaker = MutableLiveData<String>()
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
    private val _markerId = MutableLiveData<String>()
    val markerId = _markerId
    private val auth = FirebaseAuth.getInstance()
    private val _goToNext = MutableLiveData<Boolean>()
    val goToNext = _goToNext
    private val _processing = MutableLiveData<Boolean>(true)
    val processing = _processing
    private val _userId = MutableLiveData<String>()
    val userId = _userId
    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser
    private val _showToast = MutableLiveData<Boolean>()
    val showToast = _showToast
    private val _incorrectPassword = MutableLiveData<Boolean>()
    val incorrectPassword = _incorrectPassword
    private val _notRegistered = MutableLiveData<Boolean>()
    val notRegistered = _notRegistered
    private val _rememberMe = MutableLiveData<Boolean>()
    val rememberMe = _rememberMe
    private val _login = MutableLiveData<Boolean>(false)
    val login = _login
    private val _welcome = MutableLiveData<Boolean>(false)
    val welcome = _welcome
    private val _uri = MutableLiveData<Uri>()
    val uri = _uri
    private val _locationPermissionGranted = MutableLiveData<Boolean>()
    val locationPermissionGranted = _locationPermissionGranted
    private val _locationShouldShowPermissionRationale = MutableLiveData(false)
    val locationShouldShowPermissionRationale = _locationShouldShowPermissionRationale
    private val _locationShowPermissionDenied = MutableLiveData(false)
    val locationShowPermissionDenied = _locationShowPermissionDenied
    private val _isEditing = MutableLiveData<Boolean>()
    val isEditing = _isEditing
    private val _filter = MutableLiveData<String>()
    val filter = _filter

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
            else -> listaMarcadores[0]
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


    fun changePhotoMarker(photo:Uri?){
        if (photo == null) _photoMaker.value = null
        else {
            _photoMaker.value = photo.toString()
        }
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

    fun setLocationCameraPermissionGranted(granted:Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setLocationShouldShowPermissionRationale(should:Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setLocationShowPermissionDenied(denied:Boolean) {
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

    fun optionChoosed(option:String?) {
        println(_markersList.value)
        var type : String = ""
        when (option) {
            "aviongrande" -> {
                type = "avion"
                _filter.value = "aviongrande"
            }
            "gasolineragrande" -> {
                type = "gasolinera"
                _filter.value = "gasolineragrande"
            }
            "hospitalgrande" -> {
                type = "hospital"
                _filter.value = "hospitalgrande"
            }
            "hotelgrande" -> {
                type = "hotel"
                _filter.value = "hotelgrande"
            }
            "restgrande" -> {
                type = "restaurante"
                _filter.value = "restgrande"
            }
            "supergrande" -> {
                type = "supermercado"
                _filter.value = "supergrande"
            }
        }
        if (type != "") filterMarkers(type)
        else {
            _filter.value = ""
            _markersList.value = _markers.value
        }
    }

    fun filterMarkers(type:String){
        _markersList.value = _markers.value!!.filter { it.tipo == type }
    }

    fun addMarker(marker: Marca) {
        repository.addMarker(marker)
        getMarkers()
    }

    fun editMarker(marker: Marca) {
        _isEditing.value = true
        repository.editMarker(marker)
        getMarkers()
        _isEditing.value = false
    }

    fun deleteMarker(markerId:String) {
        repository.deleteMarker(markerId)
        getMarkers()
    }

    fun getMarkers() {
        repository.getMarkers().addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val list = mutableListOf<Marca>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newMarker = dc.document.toObject(Marca::class.java)
                    newMarker.markerId = dc.document.id
                    list.add(newMarker)
                }
            }
            _markers.value = list.filter { it.usuario == userId.value }
        }
    }

    fun getMarker(markerId:String) {
        repository.getMarker(markerId).addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val marker = value.toObject(Marca::class.java)
                if (marker != null) {
                    marker.markerId = markerId
                }
                _posMarker.value = LatLng(marker!!.lat, marker.lon)
                _nameMarker.value = marker.name
                _typeMarker.value = marker.tipo
                _photoMaker.value = marker.photo
            }
            else {
                println("null")
            }
        }
    }

    fun uploadImage(imageUri: Uri, marker: Marca) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("IMAGE UPLOAD", "Image uploaded successfully")
                storage.downloadUrl.addOnSuccessListener {
                    if (!_isAddImage.value!!) {
                        addMarker(
                            Marca(
                                marker.usuario,
                                "",
                                marker.lat,
                                marker.lon,
                                marker.name,
                                marker.tipo,
                                it.toString()
                            )
                        )
                    }
                    else {
                        println("hola entro por aqui")
                        editMarker(
                            Marca(
                                marker.usuario,
                                marker.markerId,
                                marker.lat,
                                marker.lon,
                                marker.name,
                                marker.tipo,
                                it.toString()
                            )
                        )
                        setIsAddImage(false)
                    }
                    _nameMarker.value = ""
                    _typeMarker.value = "avion"
                    Log.i("IMAGEN", it.toString())
                    _uri.value = null
                }
            }
            .addOnFailureListener {
                Log.e("IMAGE UPLOAD", "Image upload failed")
                addMarker(Marca(
                    userId.value!!,
                    "",
                    posMarker.value!!.latitude,
                    posMarker.value!!.longitude,
                    nameMaker.value!!,
                    typeMarker.value!!,
                    null
                ))
            }
    }

    fun changeMarkerId(markerId:String) {
        _markerId.value = markerId
    }

    fun register(username:String, password:String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                }
                else {
                    _goToNext.value = false
                }

                modifyProcessing()
            }
            .addOnFailureListener {
                Log.d("Error", "Ocurrió un error al iniciar sesión", it)
                _showToast.value = true
            }
    }

    fun modifyProcessing() {
        _processing.value = !_processing.value!!
    }

    fun login (username:String?, password:String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                }
                else {
                    _goToNext.value = false
                }
                modifyProcessing()
            }
            .addOnFailureListener {
                Log.d("Error", "Ocurrió un error al iniciar sesión", it)
                _showToast.value = true
            }
    }

    fun logout() {
        auth.signOut()
        if (_rememberMe.value == false) {
            _userId.value = ""
            _loggedUser.value = ""
        }
    }

    fun changeShowToast() {
        _showToast.value = false
    }

    fun changeRememberMe() {
        if (_rememberMe.value == null) _rememberMe.value = true
        else _rememberMe.value = !_rememberMe.value!!
    }

    fun setRememberMe(remember:Boolean) {
        _rememberMe.value = remember
    }

    fun setLogin(login:Boolean) {
        _login.value = login
    }

    fun setWelcome(welcome:Boolean) {
        _welcome.value = welcome
    }

    fun setUri(uri:Uri) {
        _uri.value = uri
    }

    fun setIsAddImage(isAddImage:Boolean) {
        _isAddImage.value = isAddImage
    }

    fun setMarker(marker: Marca) {
        _userId.value = marker.usuario
        _posMarker.value = LatLng(marker.lat, marker.lon)
        _nameMarker.value = marker.name
        _typeMarker.value = marker.tipo
        _photoMaker.value = marker.photo
    }

    /*
    fun deleteImage(image: String, marca: Marca) {
        val storage = FirebaseStorage.getInstance().getReferenceFromUrl(image)
        storage.delete()
            .addOnSuccessListener {
                marca.imagenes.remove(image)
                editMarker(marca)
            }
            .addOnFailureListener { Log.e("Image delete", "Image delete failed") }
    }
     */
}