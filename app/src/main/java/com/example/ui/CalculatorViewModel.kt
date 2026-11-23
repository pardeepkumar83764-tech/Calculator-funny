package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.R
import com.example.domain.CalcKey
import com.example.domain.EvalResult
import com.example.domain.ExpressionEvaluator
import com.example.domain.HistoryEntry
import com.example.domain.ReactionBanner
import com.example.domain.ReactionKind
import com.example.domain.SYMBOL_DIVIDE
import com.example.domain.SYMBOL_MINUS
import com.example.domain.SYMBOL_MULTIPLY
import com.example.domain.SubDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class CalculatorUiState(
    val expression: String = "",
    val isErrorDisplay: Boolean = false,
    val subDisplay: SubDisplay = SubDisplay.Empty,
    val reaction: ReactionBanner? = null,
    val history: List<HistoryEntry> = emptyList(),
) {
    val displayText: String
        get() = when {
            isErrorDisplay -> "Error"
            expression.isEmpty() -> "0"
            else -> expression
        }
}

class CalculatorViewModel(
    private val random: Random = Random.Default,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var isFreshResult = false
    private var nextReactionId = 1L
    private var nextHistoryId = 1L
    private val lastReactionByKind = mutableMapOf<ReactionKind, Int>()

    private val operators = setOf("+", SYMBOL_MINUS, SYMBOL_MULTIPLY, SYMBOL_DIVIDE)
    private val operatorSplitRegex = Regex("[+$SYMBOL_MINUS$SYMBOL_MULTIPLY$SYMBOL_DIVIDE]")
    private val trailingOpsRegex = Regex("[+$SYMBOL_MINUS$SYMBOL_MULTIPLY$SYMBOL_DIVIDE]+$")
    private val replaceTrailingOpRegex = Regex("[+$SYMBOL_MINUS$SYMBOL_MULTIPLY$SYMBOL_DIVIDE][$SYMBOL_MINUS]?$")
    private val nonExprCharRegex = Regex("[^\\d.+$SYMBOL_MINUS$SYMBOL_MULTIPLY$SYMBOL_DIVIDE%]")

    private val reactionPools: Map<ReactionKind, List<Int>> = mapOf(
        ReactionKind.NEGATIVE to listOf(
            R.string.react_neg_1,
            R.string.react_neg_2,
            R.string.react_neg_3,
            R.string.react_neg_4,
            R.string.react_neg_5,
        ),
        ReactionKind.POSITIVE to listOf(
            R.string.react_pos_1,
            R.string.react_pos_2,
            R.string.react_pos_3,
            R.string.react_pos_4,
            R.string.react_pos_5,
        ),
        ReactionKind.ZERO to listOf(
            R.string.react_zero_1,
            R.string.react_zero_2,
            R.string.react_zero_3,
        ),
        ReactionKind.ERROR to listOf(
            R.string.react_err_1,
        ),
    )

    fun onKeyPress(key: CalcKey) {
        when (key) {
            CalcKey.ZERO, CalcKey.ONE, CalcKey.TWO, CalcKey.THREE, CalcKey.FOUR,
            CalcKey.FIVE, CalcKey.SIX, CalcKey.SEVEN, CalcKey.EIGHT, CalcKey.NINE,
            -> handleDigit(key.symbol)

            CalcKey.DOT -> handleDecimal()
            CalcKey.ADD, CalcKey.SUBTRACT, CalcKey.MULTIPLY, CalcKey.DIVIDE -> handleOperator(key.symbol)
            CalcKey.PERCENT -> handlePercent()
            CalcKey.BS -> handleBackspace()
            CalcKey.AC -> handleAllClear()
            CalcKey.EQUALS -> handleEquals()
        }
    }

    fun onHistoryItemClick(entry: HistoryEntry) {
        isFreshResult = true
        _uiState.update { state ->
            state.copy(
                expression = entry.result,
                isErrorDisplay = false,
                subDisplay = SubDisplay.Empty,
                reaction = null,
            )
        }
    }

    fun onClearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    private fun lastNumberSegment(expr: String): String =
        expr.split(operatorSplitRegex).lastOrNull().orEmpty()

    private fun handleDigit(digit: String) {
        _uiState.update { state ->
            var expr = if (state.isErrorDisplay) "" else state.expression
            var sub = state.subDisplay
            if (isFreshResult) {
                expr = ""
                isFreshResult = false
                sub = SubDisplay.Empty
            }
            if (expr.endsWith("%")) return@update state
            if (lastNumberSegment(expr) == "0") {
                expr = expr.dropLast(1)
            }
            state.copy(
                expression = expr + digit,
                isErrorDisplay = false,
                subDisplay = sub,
            )
        }
    }

    private fun handleDecimal() {
        _uiState.update { state ->
            var expr = if (state.isErrorDisplay) "" else state.expression
            var sub = state.subDisplay
            if (isFreshResult) {
                expr = ""
                isFreshResult = false
                sub = SubDisplay.Empty
            }
            if (expr.endsWith("%")) return@update state
            val segment = lastNumberSegment(expr)
            if (segment.contains('.')) return@update state
            val append = if (segment.isEmpty()) "0." else "."
            state.copy(
                expression = expr + append,
                isErrorDisplay = false,
                subDisplay = sub,
            )
        }
    }

    private fun handleOperator(op: String) {
        isFreshResult = false
        _uiState.update { state ->
            val expr = if (state.isErrorDisplay) "" else state.expression
            val updatedExpr = when {
                expr.isEmpty() -> if (op == SYMBOL_MINUS) SYMBOL_MINUS else expr
                expr == SYMBOL_MINUS -> expr
                operators.contains(expr.last().toString()) -> {
                    val lastChar = expr.last().toString()
                    if (op == SYMBOL_MINUS && (lastChar == SYMBOL_MULTIPLY || lastChar == SYMBOL_DIVIDE)) {
                        expr + op
                    } else {
                        expr.replace(replaceTrailingOpRegex, "") + op
                    }
                }
                else -> expr + op
            }
            state.copy(
                expression = updatedExpr,
                isErrorDisplay = false,
            )
        }
    }

    private fun handlePercent() {
        _uiState.update { state ->
            val expr = if (state.isErrorDisplay) "" else state.expression
            val lastChar = expr.lastOrNull()
            if (lastChar != null && (lastChar.isDigit() || lastChar == '%')) {
                isFreshResult = false
                state.copy(
                    expression = "$expr%",
                    isErrorDisplay = false,
                )
            } else {
                state
            }
        }
    }

    private fun handleBackspace() {
        _uiState.update { state ->
            var sub = state.subDisplay
            if (isFreshResult) {
                isFreshResult = false
                sub = SubDisplay.Empty
            }
            val current = if (state.isErrorDisplay) "" else state.expression
            val cleaned = if (nonExprCharRegex.containsMatchIn(current)) "" else current.dropLast(1)
            state.copy(
                expression = cleaned,
                isErrorDisplay = false,
                subDisplay = sub,
            )
        }
    }

    private fun handleAllClear() {
        isFreshResult = false
        _uiState.value = _uiState.value.copy(
            expression = "",
            isErrorDisplay = false,
            subDisplay = SubDisplay.Empty,
            reaction = null,
        )
    }

    private fun handleEquals() {
        val currentState = _uiState.value
        val cleaned = currentState.expression.replace(trailingOpsRegex, "")
        if (cleaned.isEmpty() || isFreshResult) return

        when (val result = ExpressionEvaluator.evaluate(cleaned)) {
            is EvalResult.Success -> {
                val entry = HistoryEntry(
                    id = nextHistoryId++,
                    expression = cleaned,
                    result = result.formatted,
                )
                val updatedHistory = (listOf(entry) + currentState.history).take(MAX_HISTORY_ITEMS)
                val kind = when {
                    result.value < 0.0 -> ReactionKind.NEGATIVE
                    result.value > 0.0 -> ReactionKind.POSITIVE
                    else -> ReactionKind.ZERO
                }
                isFreshResult = true
                _uiState.value = currentState.copy(
                    expression = result.formatted,
                    isErrorDisplay = false,
                    subDisplay = SubDisplay.Expression("$cleaned ="),
                    reaction = createReaction(kind),
                    history = updatedHistory,
                )
            }

            EvalResult.DivisionByZero -> {
                isFreshResult = false
                _uiState.value = currentState.copy(
                    expression = "",
                    isErrorDisplay = true,
                    subDisplay = SubDisplay.Expression("$cleaned ="),
                    reaction = createReaction(ReactionKind.ERROR),
                )
            }

            EvalResult.SyntaxError -> {
                _uiState.value = currentState.copy(
                    subDisplay = SubDisplay.InvalidExpression,
                )
            }
        }
    }

    private fun createReaction(kind: ReactionKind): ReactionBanner {
        val pool = reactionPools.getValue(kind)
        val previous = lastReactionByKind[kind]
        val candidates = if (pool.size > 1 && previous != null) {
            pool.filter { it != previous }
        } else {
            pool
        }
        val picked = candidates[random.nextInt(candidates.size)]
        lastReactionByKind[kind] = picked
        return ReactionBanner(
            id = nextReactionId++,
            kind = kind,
            messageResId = picked,
        )
    }

    companion object {
        private const val MAX_HISTORY_ITEMS = 20
    }
}
