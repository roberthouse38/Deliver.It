package com.example.deliverit

import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity

@Suppress("PreviewAnnotationInFunctionWithParameters")
class test : ComponentActivity() {

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""
        setContent {
            homepage(username)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun homepage(username: String) {
        // Menambahkan state untuk menyimpan deskripsi
        var description by remember { mutableStateOf("") }

        Scaffold(bottomBar = { BottomNavigationBar(username) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Welcome $username !",
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                )

                Text(
                    text = "Take a look at the items at the warehouse",
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                )

                Text(
                    text = "Tap the image to show the description",
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                )

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = com.example.deliverit.R.drawable.glass),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clickable {
                                // Menambahkan deskripsi saat gambar diklik
                                description = "Any type of glass of you want. And any thickness too!"
                            }
                    )

                    Image(
                        painter = painterResource(id = com.example.deliverit.R.drawable.plastic),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clickable {
                                // Menambahkan deskripsi saat gambar diklik
                                description = "Brand new plastic, fresh from the pabric with 100% eco-firendly process"
                            }
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = com.example.deliverit.R.drawable.galvanizedsteel),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clickable {
                                // Menambahkan deskripsi saat gambar diklik
                                description = "Strong steel for manufacture"
                            }
                    )

                    Image(
                        painter = painterResource(id = com.example.deliverit.R.drawable.wood),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clickable {
                                // Menambahkan deskripsi saat gambar diklik
                                description = "Woods chopped from nearest forest, and the lumber mill"
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Menampilkan deskripsi
                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }

    @Composable
    fun BottomNavigationBar(username: String) {
        NavigationBar(modifier = Modifier) {
            NavigationBarItem(
                selected = true,
                onClick = { /* Already on Home page */ },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = null
                    )
                },
                label = { Text(text = "Home") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@test, Library::class.java).apply {
                        putExtra(EXTRA_USERNAME, username)
                    }
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.library),
                        contentDescription = null
                    )
                },
                label = { Text(text = "Stock") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@test, notes::class.java)
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_notes_24),
                        contentDescription = null
                    )
                },
                label = { Text(text = "Notes") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@test, ProfileAccountPage::class.java).apply {
                        putExtra(ProfileAccountPage.EXTRA_USERNAME, username)
                    }
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.account_circle),
                        contentDescription = null
                    )
                },
                label = { Text(text = "Profile") }
            )
        }
    }

    private fun navigateToPage(java: Class<*>) {
        val intent = Intent(this, java)
        startActivity(intent)
    }
}
