package com.example.androidcalculadora.ui.theme

import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidcalculadora.CalculatorLogic

@Composable
fun CalculatorLayout() {
    var display by remember { mutableStateOf("0") }
    var currentNumber by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var pendingOperator by remember { mutableStateOf<String?>(null) }

    fun onButtonClick(text: String) {
        when (text) {
            "C" -> {
                display = "0"
                currentNumber = ""
                result = ""
                pendingOperator = null
            }
            "+", "-", "×", "÷" -> {
                if (result.isNotEmpty()) {
                    display = result
                }
                pendingOperator = text
                currentNumber = ""
            }
            else -> {
                currentNumber += text
                if (pendingOperator != null && display != "0") {
                    result = CalculatorLogic.calculate("${display}${pendingOperator}${currentNumber}")
                    display = result
                } else {
                    display = currentNumber
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(45.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(100.dp)
                .background(Color(0xFFB2DFDB), shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display.trimEnd('0').trimEnd('.'),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val buttons = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("0", "C", "+")
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttons.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEachIndexed { colIndex, text ->
                        if (text.isNotEmpty()) {
                            Button(
                                onClick = { onButtonClick(text) },
                                modifier = Modifier
                                    .weight(if (rowIndex == 3 && colIndex == 0) 2f else 1f)
                                    .height(80.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (text in listOf("+", "-", "×", "÷")) Color(0xFFFFA726) else Color(0xFF546E7A),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = text,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
