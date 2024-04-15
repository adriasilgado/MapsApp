package com.example.mapsapp.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navigationController: NavController, myViewModel: MyViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var secondPassword by remember { mutableStateOf("") }
    val showToast by myViewModel.showToast.observeAsState()
    var passwordVisibility by remember { mutableStateOf(false) }
    var secondPasswordVisibility by remember { mutableStateOf(false) }
    var emptyName by remember { mutableStateOf(true) }
    var emptyEmail by remember { mutableStateOf(true) }
    var emptyPassword by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        OutlinedTextField(
            value = name,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            onValueChange = {
                if (it.isEmpty()) emptyName = true
                else emptyName = false
                name = it},
            label = { Text("Enter name", fontFamily = sky) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.Black
            ))
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
        OutlinedTextField(
            value = secondPassword,
            onValueChange = { newPassword ->
                if (newPassword.isEmpty()) emptyPassword = true
                else emptyPassword = false
                secondPassword = newPassword
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            label = { Text("Repeat password", fontFamily = sky) },
            visualTransformation = if (secondPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { secondPasswordVisibility = !secondPasswordVisibility }) {
                    Icon(
                        imageVector = if (secondPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (secondPasswordVisibility) "Hide password" else "Show password"
                    )
                }
            }
        )
        Button(
            onClick = {
                      if (password.length >= 6) {
                          if (password == secondPassword) {
                              myViewModel.register(email, password)
                              if (showToast == false) {
                                  navigationController.navigate(Routes.LoginScreen.route)
                              }
                              else {
                                  Toast.makeText(context, "Email ya registrado", Toast.LENGTH_SHORT).show()
                                  myViewModel.changeShowToast()
                              }
                          }
                          else {
                              Toast.makeText(context, "Las contraseñas no son iguales.", Toast.LENGTH_SHORT).show()
                          }
                      }
                      else {
                          Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                      }},
            modifier = Modifier
                .fillMaxHeight(0.15f)
                .width(150.dp)
                .padding(top = 20.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Color.DarkGray),
            enabled = !emptyEmail && !emptyPassword
        ){
            Text("Register!", fontFamily = sky)
        }
        if (showToast == true) {
            Toast.makeText(context, "Email ya registrado", Toast.LENGTH_SHORT).show()
            myViewModel.changeShowToast()
        }
    }
}