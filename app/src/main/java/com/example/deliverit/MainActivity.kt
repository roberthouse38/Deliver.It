package com.example.deliverit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DbHelper(this) //initiate DbHelper
        setContent {
            loginPage()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun loginPage() {

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.deliverlogo),
                contentDescription = "Logo DeliverIt",
                modifier = Modifier.height(200.dp),
            )

            Text(
                text = "DELIVER.IT",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "LogIn Page",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Username/Email") }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") }
            )

            Button(onClick = {
                logIn(email, password)
            }) {
                Text(text = "Sign In")
            }

            TextButton(onClick = {
                navigateToSignUp()
            }) {
                Text(text = "Don't Have an Account?")
            }
        }
    }

    private fun logIn(email: String, password: String) {
        val userExists = dbHelper.readUser(email, password)
        if (userExists) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, test::class.java).apply {
                putExtra(test.EXTRA_USERNAME, email) // Pass username to test Activity
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "test") {
            composable("test") { test() }
            composable("library") { Library() }
        }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, signin::class.java)
        startActivity(intent)
    }

}

