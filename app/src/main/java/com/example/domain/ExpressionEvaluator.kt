package com.example.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.abs

/**
 * Safe recursive-descent arithmetic evaluator supporting +, −, ×, ÷, unary +/-, and postfix %.
 * Mirrors the exact grammar and precision rules of Hisaab Kitaab Calculator.
 */
object ExpressionEvaluator {

    private val tokenRegex = Regex("""\d*\.\d+|\d+\.?|[+$SYMBOL_MINUS$SYMBOL_MULTIPLY$SYMBOL_DIVIDE%]""")

    private class SyntaxException : RuntimeException()
    private class DivByZeroException : RuntimeException()

    fun evaluate(expression: String): EvalResult {
        val tokens = tokenRegex.findAll(expression).map { it.value }.toList()
        if (tokens.isEmpty()) return EvalResult.SyntaxError

        // Ensure no unrecognized characters were silently skipped
        val matchedLength = tokens.sumOf { it.length }
        if (matchedLength != expression.length) return EvalResult.SyntaxError

        var index = 0

        fun peek(): String? = tokens.getOrNull(index)

        fun number(): Double {
            val token = tokens.getOrNull(index++) ?: throw SyntaxException()
            val firstChar = token.firstOrNull() ?: throw SyntaxException()
            if (!firstChar.isDigit() && firstChar != '.') throw SyntaxException()

            var value = token.toDoubleOrNull() ?: throw SyntaxException()
            while (peek() == "%") {
                index++
                value /= 100.0
            }
            return value
        }

        fun unary(): Double {
            return when (peek()) {
                SYMBOL_MINUS -> {
                    index++
                    -unary()
                }
                "+" -> {
                    index++
                    unary()
                }
                else -> number()
            }
        }

        fun term(): Double {
            var value = unary()
            while (peek() == SYMBOL_MULTIPLY || peek() == SYMBOL_DIVIDE) {
                val op = tokens[index++]
                val right = unary()
                if (op == SYMBOL_DIVIDE) {
                    if (right == 0.0) throw DivByZeroException()
                    value /= right
                } else {
                    value *= right
                }
            }
            return value
        }

        fun sum(): Double {
            var value = term()
            while (peek() == "+" || peek() == SYMBOL_MINUS) {
                val op = tokens[index++]
                val right = term()
                value = if (op == "+") value + right else value - right
            }
            return value
        }

        return try {
            val result = sum()
            if (index < tokens.size) {
                EvalResult.SyntaxError
            } else if (!result.isFinite()) {
                EvalResult.DivisionByZero
            } else {
                val normalized = if (result == 0.0) 0.0 else result
                EvalResult.Success(
                    value = normalized,
                    formatted = formatNumber(normalized),
                )
            }
        } catch (_: DivByZeroException) {
            EvalResult.DivisionByZero
        } catch (_: SyntaxException) {
            EvalResult.SyntaxError
        }
    }

    fun formatNumber(value: Double): String {
        if (!value.isFinite()) return "Error"
        val clean = if (value == 0.0) 0.0 else value
        val rounded = BigDecimal(clean, MathContext(12, RoundingMode.HALF_UP))
            .stripTrailingZeros()

        val absVal = abs(rounded.toDouble())
        val raw = when {
            absVal == 0.0 -> "0"
            absVal >= 1e15 || absVal < 1e-6 -> {
                rounded.toString().replace('E', 'e')
            }
            else -> rounded.toPlainString()
        }
        return raw.replace("-", SYMBOL_MINUS)
    }
}
