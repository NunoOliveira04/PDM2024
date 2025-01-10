package com.example.androidloja2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults


data class CartItem(
    val marca: String,
    val modelo: String,
    val tamanho: Int,
    val quantidade: Int,
    val preco: Double,
    val imagem: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartSheet(
    cartItems: List<CartItem>,
    userId: String,
    onDismiss: () -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onClearCart: () -> Unit,
    onImportCart: (String) -> Unit,
    onExportCart: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var importCarrinhoId by remember { mutableStateOf("") }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Importar Carrinho", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = importCarrinhoId,
                    onValueChange = { importCarrinhoId = it },
                    label = { Text("Número do Carrinho", color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )
            },
            containerColor = Color.Black,
            confirmButton = {
                Button(onClick = {
                    if (importCarrinhoId.isNotEmpty()) {
                        onImportCart(importCarrinhoId)
                        showImportDialog = false
                        importCarrinhoId = ""
                    }
                }) {
                    Text("Importar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exportar Carrinho", color = Color.White) },
            text = {
                Text(
                    "O número do seu carrinho é: $userId",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            containerColor = Color.Black,
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.Black),
        drawerContainerColor = Color.Black,
        windowInsets = WindowInsets(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Carrinho",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Mais opções",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.DarkGray)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Limpar Carrinho", color = Color.White) },
                                onClick = {
                                    onClearCart()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Importar Carrinho", color = Color.White) },
                                onClick = {
                                    showImportDialog = true
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Exportar Carrinho", color = Color.White) },
                                onClick = {
                                    showExportDialog = true
                                    showMenu = false
                                }
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.White
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(item = item, onRemoveItem = { onRemoveItem(item) })
                    Divider(color = Color.White.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val total = cartItems.sumOf { it.preco * it.quantidade }
            Text(
                text = "Total: €${String.format("%.2f", total)}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemoveItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imagem,
            contentDescription = "${item.marca} ${item.modelo}",
            modifier = Modifier
                .size(80.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${item.marca} ${item.modelo}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tamanho: ${item.tamanho}",
                color = Color.LightGray
            )
            Text(
                text = "Quantidade: ${item.quantidade}",
                color = Color.LightGray
            )
            Text(
                text = "€${String.format("%.2f", item.preco)}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(
            onClick = onRemoveItem,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remover item",
                tint = Color.White
            )
        }
    }
}