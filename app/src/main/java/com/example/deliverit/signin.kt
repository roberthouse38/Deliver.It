package com.example.deliverit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class signin : ComponentActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DbHelper(this) //initiate DbHelper
        setContent {
            signpage()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun signpage(){

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
                text = "SignIn Page",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Input Username") }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Input Password") }
            )

            Button(onClick = {
                signUp(email,password)
            }) {
                Text(text = "Sign In")
            }
        }
    }

    fun signUp(email: String, password: String) {
        val result = dbHelper.insertUser(email, password)
        if (result > 0) {
            Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
            finish() // Menutup activity ini dan kembali ke login
        } else {
            Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show()
        }
    }
}