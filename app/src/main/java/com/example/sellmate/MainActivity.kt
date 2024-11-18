@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.sellmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.sellmate.ui.theme.SellMateTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.sellmate.page.ProdukScreen
import com.example.sellmate.page.ProfileScreen

import com.example.sellmate.page.db



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SellMateTheme {
                val navController = rememberNavController()

                // Menyusun NavHost untuk navigasi antar halaman
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("profile") { ProfileScreen() }
                    // Tambahkan halaman lain seperti history atau produk sesuai kebutuhan
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(0) }
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SearchBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog.value = true },  // Set openDialog ke true saat tombol Add ditekan
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Product")
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = { selectedIndex.value = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (selectedIndex.value == 0) {
                Text("Selamat Datang Di Aplikasi SellMate")
            } else if (selectedIndex.value == 1) {
                ProdukScreen() // Menampilkan daftar produk
            }
        }

        // Tampilkan dialog jika openDialog bernilai true
        if (openDialog.value) {
            ProductInputDialog(
                onFinish = { name, type, price, quantity ->
                    addProductToFirestore(name, type, price, quantity)
                    openDialog.value = false  // Tutup dialog setelah penyimpanan selesai
                },
                onDismiss = { openDialog.value = false } // Tutup dialog jika dibatalkan
            )
        }
    }
}

fun addProductToFirestore(name: String, type: String, price: Double, quantity: Int) {
    // Membuat map data untuk disimpan di Firestore
    val productData = hashMapOf(
        "name" to name,
        "type" to type,
        "price" to price,
        "quantity" to quantity
    )

    // Menambahkan data produk ke koleksi "products" di Firestore
    db.collection("products")
        .add(productData)
        .addOnSuccessListener { documentReference ->
            // Jika berhasil, tampilkan pesan atau lakukan tindakan lain
            println("Produk berhasil ditambahkan dengan ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            // Jika gagal, tampilkan pesan kesalahan
            println("Gagal menambahkan produk: $e")
        }
}

@Composable
fun ProductInputDialog(onFinish: (String, String, Double, Int) -> Unit, onDismiss: () -> Unit) {
    val name = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Tambah Produk") },
        text = {
            Column {
                TextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nama Produk") })
                TextField(value = type.value, onValueChange = { type.value = it }, label = { Text("Tipe Produk") })
                TextField(value = price.value, onValueChange = { price.value = it }, label = { Text("Harga Produk") })
                TextField(value = quantity.value, onValueChange = { quantity.value = it }, label = { Text("Jumlah Produk") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.value.isNotEmpty() && type.value.isNotEmpty() && price.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                    onFinish(name.value, type.value, price.value.toDouble(), quantity.value.toInt())
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun SearchBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = "",
            onValueChange = { /* Handle search text change */ },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search Icon")
            },
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEADAB7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(onClick = {
            navController.navigate("profile") // Navigasi ke halaman Profil
        }) {
            Icon(Icons.Filled.Person, contentDescription = "Profile Icon")
        }
    }
}

@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF8DA7CC)
    ) {
        val selectedIconColor = Color.Black
        val unselectedIconColor = Color(0xFF4A4A4A)

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (selectedIndex == 0) selectedIconColor else unselectedIconColor
                )
            },
            label = { Text("Home") },
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.ShoppingBag,
                    contentDescription = "Products",
                    tint = if (selectedIndex == 1) selectedIconColor else unselectedIconColor
                )
            },
            label = { Text("Products") },
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.History,
                    contentDescription = "History",
                    tint = if (selectedIndex == 2) selectedIconColor else unselectedIconColor
                )
            },
            label = { Text("History") },
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SellMateTheme {
        HomeScreen(navController = rememberNavController())
    }
}
