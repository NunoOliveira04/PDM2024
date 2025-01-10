package com.example.androidloja2.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.androidloja2.model.Product
import androidx.compose.foundation.BorderStroke
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidloja2.ui.screens.main.MainViewModel
import com.example.androidloja2.ui.components.CartSheet
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit,
    onAddToCart: (Product, Int) -> Unit,
    viewModel: MainViewModel
) {
    var selectedSize by remember { mutableStateOf<Int?>(null) }
    val isCartOpen by viewModel.isCartOpen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)


    LaunchedEffect(isCartOpen) {
        if (isCartOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            if (isCartOpen) {
                CartSheet(
                    cartItems = cartItems,
                    userId = viewModel.getUserId(),
                    onDismiss = { viewModel.toggleCart() },
                    onRemoveItem = { item -> viewModel.removeFromCart(item) },
                    onClearCart = { viewModel.clearCart() },
                    onImportCart = { carrinhoId -> viewModel.importCart(carrinhoId) },
                    onExportCart = { }
                )
            }
        },
        gesturesEnabled = true,
        drawerState = drawerState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Black,
                topBar = {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { viewModel.toggleCart() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrinho",
                            tint = Color.White
                        )
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = product.imagem,
                            contentDescription = product.modelo,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${product.marca} ${product.modelo}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.genero,
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )

                    Text(
                        text = product.categoria,
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "€${product.preco}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Tamanho",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(140.dp)
                    ) {
                        items(product.tamanhos) { size ->
                            SizeButton(
                                size = size,
                                isSelected = selectedSize == size,
                                onClick = { selectedSize = size }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            selectedSize?.let { size ->
                                viewModel.addToCart(product, size)
                                showSnackbar = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = selectedSize != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = "Adicionar ao Carrinho",
                            color = Color.White
                        )
                    }
                }
            }

            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                ) {
                    Text("Produto adicionado ao carrinho")
                }

                LaunchedEffect(showSnackbar) {
                    delay(2000)
                    showSnackbar = false
                }
            }
        }
    }
}

@Composable
fun SizeButton(
    size: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
        )
    ) {
        Text(
            text = size.toString(),
            fontSize = 16.sp
        )
    }
}