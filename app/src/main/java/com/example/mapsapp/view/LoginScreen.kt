package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.data.UserPrefs
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navigationController: NavController, myViewModel: MyViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val goToNext by myViewModel.goToNext.observeAsState()
    val showToast by myViewModel.showToast.observeAsState(false)
    val rememberMe by myViewModel.rememberMe.observeAsState()
    println("remember: $rememberMe")
    val login by myViewModel.login.observeAsState(false)
    var emptyEmail by remember { mutableStateOf(true) }
    var emptyPassword by remember { mutableStateOf(true) }
    var context = LocalContext.current
    val userPrefs = UserPrefs(context)
    var enter by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false)}

    if (!enter) {
        println("entre al if")
        enter = true
        CoroutineScope(Dispatchers.Main).launch {
            userPrefs.getUserData.collect { data ->
                if (data[2] == "true") {
                    loading = true
                    println("datos: $data")
                    myViewModel.login(data[0], data[1])
                    withContext(Dispatchers.Main) {
                        myViewModel.setRememberMe(true)
                    }
                    delay(1000)
                    if (goToNext == true) {
                        withContext(Dispatchers.Main) {
                            navigationController.navigate(Routes.MapScreen.route)
                        }
                    }
                }
                else if (rememberMe == true) {
                    println("aqui entro al else if")
                    email = data[0]
                    password = data[1]
                }
            }
        }
    }

    if (!loading) {
        Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
            Box(modifier = Modifier.fillMaxHeight(0.3f)) {
                Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                    OutlinedTextField(
                        value = email,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        onValueChange = {
                            if (it.isEmpty()) emptyEmail = true
                            else emptyEmail = false
                            email = it},
                        label = { Text("Enter email", fontFamily = sky) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.Black
                        ))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { newPassword ->
                            if (newPassword.isEmpty()) emptyPassword = true
                            else emptyPassword = false
                            password = newPassword
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text("Password", fontFamily = sky) },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Icon(
                                    imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    Text(text = "Don't have an account? Sign up!", fontFamily = sky, modifier = Modifier
                        .clickable { navigationController.navigate(Routes.SignUpScreen.route) }
                        .padding(top = 10.dp), color = Color.Gray)
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Checkbox(checked = rememberMe ?: false, onCheckedChange = { myViewModel.changeRememberMe() })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remember Me!", Modifier.align(CenterVertically))
                    }
                }

            }

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(email, password, rememberMe.toString())
                        withContext(Dispatchers.Main) {
                            myViewModel.setLogin(true)
                        }
                        val storedUserData = userPrefs.getUserData.first()
                        if (storedUserData.isNotEmpty() && storedUserData[0] != "" && storedUserData[1] != "" && login == true && showToast == false) {
                            withContext(Dispatchers.Main) {
                                myViewModel.setLogin(false)
                                loading = true
                            }
                            myViewModel.login(storedUserData[0], storedUserData[1])
                            delay(1000)
                            if (goToNext == true) {
                                withContext(Dispatchers.Main) {
                                    navigationController.navigate(Routes.MapScreen.route)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .width(150.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                enabled = (emptyEmail == false && emptyPassword == false) || (email.length > 0 && password.length > 0)){
                Text("Login!", fontFamily = sky)
            }
            if (showToast == true) {
                myViewModel.changeShowToast()
                context = LocalContext.current
                Toast.makeText(context, "Usuario/Contraseña incorrecto.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    else {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            CircularProgressIndicator(color = Color.Black, strokeWidth = 10.dp, modifier = Modifier.size(100.dp))
        }
    }

}