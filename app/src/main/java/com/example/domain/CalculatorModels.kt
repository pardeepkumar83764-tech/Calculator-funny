package com.example.domain

import androidx.annotation.StringRes

const val SYMBOL_MINUS = "\u2212"
const val SYMBOL_MULTIPLY = "\u00D7"
const val SYMBOL_DIVIDE = "\u00F7"

enum class ReactionKind {
    NEGATIVE,
    POSITIVE,
    ZERO,
    ERROR,
}

data class ReactionBanner(
    val id: Long,
    val kind: ReactionKind,
    @StringRes val messageResId: Int,
)

data class HistoryEntry(
    val id: Long,
    val expression: String,
    val result: String,
)

enum class KeyStyle {
    STANDARD,
    OPERATOR,
    ALL_CLEAR,
    EQUALS,
}

enum class CalcKey(
    val symbol: String,
    val testTag: String,
    val style: KeyStyle = KeyStyle.STANDARD,
) {
    AC("AC", "key_ac", KeyStyle.ALL_CLEAR),
    BS("\u232B", "key_bs", KeyStyle.STANDARD),
    PERCENT("%", "key_percent", KeyStyle.STANDARD),
    DIVIDE(SYMBOL_DIVIDE, "key_div", KeyStyle.OPERATOR),

    SEVEN("7", "key_7"),
    EIGHT("8", "key_8"),
    NINE("9", "key_9"),
    MULTIPLY(SYMBOL_MULTIPLY, "key_mul", KeyStyle.OPERATOR),

    FOUR("4", "key_4"),
    FIVE("5", "key_5"),
    SIX("6", "key_6"),
    SUBTRACT(SYMBOL_MINUS, "key_sub", KeyStyle.OPERATOR),

    ONE("1", "key_1"),
    TWO("2", "key_2"),
    THREE("3", "key_3"),
    ADD("+", "key_add", KeyStyle.OPERATOR),

    ZERO("0", "key_0"),
    DOT(".", "key_dot"),
    EQUALS("=", "key_eq", KeyStyle.EQUALS),
}

sealed interface EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult
    data object DivisionByZero : EvalResult
    data object SyntaxError : EvalResult
}

sealed interface SubDisplay {
    data object Empty : SubDisplay
    data class Expression(val text: String) : SubDisplay
    data object InvalidExpression : SubDisplay
}
