package com.example.appresina.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appresina.model.Producto
import com.example.appresina.viewmodel.ProductoViewModel

@Composable
fun ProductoScreen(viewModel: ProductoViewModel) {
    val productos by viewModel.productoList.collectAsState()
    ProductoScreenContent(productos = productos)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreenContent(productos: List<Producto>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Productos") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(productos) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Título: ${producto.nombre}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = producto.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoScreenPreview() {
    val previewProductos = listOf(
        Producto(id = 1, nombre = "Producto de Avance 1", tipo = "Tipo A", precio = 19.99, cantidad = 10, descripcion = "Esta es una descripción para el producto de avance 1.", usuarioId = 1),
        Producto(id = 2, nombre = "Producto de Avance 2", tipo = "Tipo B", precio = 29.99, cantidad = 5, descripcion = "Esta es una descripción para el producto de avance 2.", usuarioId = 1),
        Producto(id = 3, nombre = "Producto de Avance 3", tipo = "Tipo A", precio = 39.99, cantidad = 8, descripcion = "Esta es una descripción para el producto de avance 3.", usuarioId = 1)
    )
    ProductoScreenContent(productos = previewProductos)
}