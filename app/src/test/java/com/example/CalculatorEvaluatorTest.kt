package com.example

import com.example.domain.CalcKey
import com.example.domain.EvalResult
import com.example.domain.ExpressionEvaluator
import com.example.domain.ReactionKind
import com.example.domain.SYMBOL_DIVIDE
import com.example.domain.SYMBOL_MINUS
import com.example.domain.SYMBOL_MULTIPLY
import com.example.ui.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEvaluatorTest {

    @Test
    fun `evaluates operator precedence and decimals accurately`() {
        val r1 = ExpressionEvaluator.evaluate("2+3${SYMBOL_MULTIPLY}4") as EvalResult.Success
        assertEquals("14", r1.formatted)

        val r2 = ExpressionEvaluator.evaluate("0.1+0.2") as EvalResult.Success
        assertEquals("0.3", r2.formatted)

        val r3 = ExpressionEvaluator.evaluate("200${SYMBOL_MULTIPLY}15%") as EvalResult.Success
        assertEquals("30", r3.formatted)

        val r4 = ExpressionEvaluator.evaluate("5${SYMBOL_MULTIPLY}${SYMBOL_MINUS}3") as EvalResult.Success
        assertEquals("${SYMBOL_MINUS}15", r4.formatted)

        val r5 = ExpressionEvaluator.evaluate("${SYMBOL_MINUS}0") as EvalResult.Success
        assertEquals("0", r5.formatted)
    }

    @Test
    fun `detects division by zero`() {
        val result = ExpressionEvaluator.evaluate("10${SYMBOL_DIVIDE}0")
        assertTrue(result is EvalResult.DivisionByZero)
    }

    @Test
    fun `viewmodel produces correct reactions and manages history`() {
        val vm = CalculatorViewModel()

        // 5 - 12 = -7 (NEGATIVE)
        vm.onKeyPress(CalcKey.FIVE)
        vm.onKeyPress(CalcKey.SUBTRACT)
        vm.onKeyPress(CalcKey.ONE)
        vm.onKeyPress(CalcKey.TWO)
        vm.onKeyPress(CalcKey.EQUALS)

        assertEquals("${SYMBOL_MINUS}7", vm.uiState.value.displayText)
        assertEquals(ReactionKind.NEGATIVE, vm.uiState.value.reaction?.kind)
        assertEquals(1, vm.uiState.value.history.size)

        // 10 + 5 = 15 (POSITIVE)
        vm.onKeyPress(CalcKey.ONE)
        vm.onKeyPress(CalcKey.ZERO)
        vm.onKeyPress(CalcKey.ADD)
        vm.onKeyPress(CalcKey.FIVE)
        vm.onKeyPress(CalcKey.EQUALS)

        assertEquals("15", vm.uiState.value.displayText)
        assertEquals(ReactionKind.POSITIVE, vm.uiState.value.reaction?.kind)
        assertEquals(2, vm.uiState.value.history.size)

        // Recall first history item (-7)
        val negEntry = vm.uiState.value.history.last()
        vm.onHistoryItemClick(negEntry)
        assertEquals("${SYMBOL_MINUS}7", vm.uiState.value.displayText)
        assertNull(vm.uiState.value.reaction)

        // Add 7 => 0 (ZERO)
        vm.onKeyPress(CalcKey.ADD)
        vm.onKeyPress(CalcKey.SEVEN)
        vm.onKeyPress(CalcKey.EQUALS)
        assertEquals("0", vm.uiState.value.displayText)
        assertEquals(ReactionKind.ZERO, vm.uiState.value.reaction?.kind)

        // 9 ÷ 0 => Error (ERROR)
        vm.onKeyPress(CalcKey.NINE)
        vm.onKeyPress(CalcKey.DIVIDE)
        vm.onKeyPress(CalcKey.ZERO)
        vm.onKeyPress(CalcKey.EQUALS)
        assertTrue(vm.uiState.value.isErrorDisplay)
        assertNotNull(vm.uiState.value.reaction)
        assertEquals(ReactionKind.ERROR, vm.uiState.value.reaction?.kind)

        // Clear history
        vm.onClearHistory()
        assertTrue(vm.uiState.value.history.isEmpty())
    }
}
