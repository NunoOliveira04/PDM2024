package com.example.androidcalculadora

object CalculatorLogic {
    fun calculate(expression: String): String {
        return try {
            val result = eval(expression)
            if (result % 1 == 0.0) result.toInt().toString() else result.toString() // Sem ".0" para números inteiros
        } catch (e: Exception) {
            "Erro"
        }
    }

    private fun eval(expression: String): Double {
        val tokens = expression.split("(?<=[-+×÷])|(?=[-+×÷])".toRegex()) // Divide pelos operadores
        var result = tokens[0].toDouble()

        var index = 1
        while (index < tokens.size) {
            val operator = tokens[index]
            val value = tokens[index + 1].toDouble()

            result = when (operator) {
                "+" -> result + value
                "-" -> result - value
                "×" -> result * value
                "÷" -> result / value
                else -> result
            }

            index += 2
        }
        return result
    }
}
