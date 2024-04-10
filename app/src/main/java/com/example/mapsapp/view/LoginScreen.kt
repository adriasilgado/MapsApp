package com.example.mapsapp.view

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.sky
import com.example.mapsapp.viewModel.MyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navigationController: NavController, myViewModel: MyViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val press by myViewModel.press.observeAsState()
    val goToNext by myViewModel.goToNext.observeAsState()
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
        OutlinedTextField(
            value = email,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            onValueChange = {
                if (it.isEmpty()) myViewModel.changePress(false)
                else myViewModel.changePress(true)
               email = it},
            label = { Text("Enter email", fontFamily = sky) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.Black
            ))
        OutlinedTextField(
            value = password,
            onValueChange = { newPassword ->
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
        Button(
            onClick = {
                myViewModel.login(email, password)
                      if (goToNext == true) navigationController.navigate(Routes.MapScreen.route) },
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .width(150.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Color.DarkGray)) {
            Text("Login!", fontFamily = sky)
        }
    }
}