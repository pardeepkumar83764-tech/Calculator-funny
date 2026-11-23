package com.example.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.domain.CalcKey
import com.example.domain.HistoryEntry
import com.example.domain.SubDisplay
import com.example.ui.components.AmbientBackground
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.HistoryPanel
import com.example.ui.components.ReactionBannerCard
import com.example.ui.theme.HisaabTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showHistorySheet by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AmbientBackground(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                val mapped = mapHardwareKeyEvent(event)
                if (mapped != null) {
                    viewModel.onKeyPress(mapped)
                    true
                } else {
                    false
                }
            },
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            val isExpandedLayout = maxWidth >= 600.dp

            if (isExpandedLayout) {
                ExpandedCalculatorLayout(
                    uiState = uiState,
                    availableHeight = maxHeight,
                    onKeyPress = viewModel::onKeyPress,
                    onHistorySelect = viewModel::onHistoryItemClick,
                    onClearHistory = viewModel::onClearHistory,
                )
            } else {
                CompactCalculatorLayout(
                    uiState = uiState,
                    availableHeight = maxHeight,
                    onKeyPress = viewModel::onKeyPress,
                    onOpenHistory = { showHistorySheet = true },
                )
            }
        }
    }

    if (showHistorySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = sheetState,
            containerColor = HisaabTheme.colors.sheetBackground,
            contentColor = HisaabTheme.colors.textPrimary,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 340.dp, max = 520.dp)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                HistoryPanel(
                    history = uiState.history,
                    onSelectEntry = { entry ->
                        viewModel.onHistoryItemClick(entry)
                        showHistorySheet = false
                    },
                    onClearHistory = viewModel::onClearHistory,
                )
            }
        }
    }
}

@Composable
private fun CompactCalculatorLayout(
    uiState: CalculatorUiState,
    availableHeight: Dp,
    onKeyPress: (CalcKey) -> Unit,
    onOpenHistory: () -> Unit,
) {
    val keyHeight = when {
        availableHeight < 640.dp -> 54.dp
        availableHeight < 760.dp -> 62.dp
        else -> 68.dp
    }
    val keySpacing = if (availableHeight < 640.dp) 10.dp else 12.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 460.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeaderBar(
            historyCount = uiState.history.size,
            showHistoryButton = true,
            onOpenHistory = onOpenHistory,
        )

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                CalculatorDisplaySection(
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                )

                Spacer(modifier = Modifier.height(10.dp))

                ReactionBannerCard(reaction = uiState.reaction)

                Spacer(modifier = Modifier.height(14.dp))

                CalculatorKeypad(
                    onKeyPress = onKeyPress,
                    keyHeight = keyHeight,
                    spacing = keySpacing,
                )
            }
        }
    }
}

@Composable
private fun ExpandedCalculatorLayout(
    uiState: CalculatorUiState,
    availableHeight: Dp,
    onKeyPress: (CalcKey) -> Unit,
    onHistorySelect: (HistoryEntry) -> Unit,
    onClearHistory: () -> Unit,
) {
    val keyHeight = when {
        availableHeight < 520.dp -> 48.dp
        availableHeight < 660.dp -> 56.dp
        else -> 64.dp
    }
    val keySpacing = if (availableHeight < 560.dp) 8.dp else 12.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 900.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeaderBar(
            historyCount = uiState.history.size,
            showHistoryButton = false,
            onOpenHistory = {},
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassCard(
                modifier = Modifier
                    .widthIn(min = 340.dp, max = 410.dp)
                    .weight(1.1f)
                    .fillMaxHeight(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    CalculatorDisplaySection(
                        uiState = uiState,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ReactionBannerCard(reaction = uiState.reaction)
                    CalculatorKeypad(
                        onKeyPress = onKeyPress,
                        keyHeight = keyHeight,
                        spacing = keySpacing,
                    )
                }
            }

            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                ) {
                    HistoryPanel(
                        history = uiState.history,
                        onSelectEntry = onHistorySelect,
                        onClearHistory = onClearHistory,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderBar(
    historyCount: Int,
    showHistoryButton: Boolean,
    onOpenHistory: () -> Unit,
) {
    val colors = HisaabTheme.colors
    val openHistoryDesc = stringResource(R.string.cd_open_history)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.operatorBg.copy(alpha = 0.18f))
                    .border(1.dp, colors.operatorBg.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Calculate,
                    contentDescription = null,
                    tint = colors.equalsStart,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        }

        if (showHistoryButton) {
            val pillShape = RoundedCornerShape(14.dp)
            Row(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(pillShape)
                    .background(colors.glassCard)
                    .border(1.dp, colors.glassBorder, pillShape)
                    .clickable(
                        role = Role.Button,
                        onClick = onOpenHistory,
                    )
                    .semantics { contentDescription = openHistoryDesc }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("open_history_button"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = colors.equalsStart,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.label_history),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textPrimary,
                )
                if (historyCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.operatorBg)
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = historyCount.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.operatorText,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorDisplaySection(
    uiState: CalculatorUiState,
    modifier: Modifier = Modifier,
) {
    val colors = HisaabTheme.colors
    val displayText = if (uiState.isErrorDisplay) {
        stringResource(R.string.label_error)
    } else {
        uiState.displayText
    }
    val subText = when (val sub = uiState.subDisplay) {
        SubDisplay.Empty -> ""
        is SubDisplay.Expression -> sub.text
        SubDisplay.InvalidExpression -> stringResource(R.string.label_invalid_expression)
    }

    val fontSize = when {
        uiState.isErrorDisplay -> 46.sp
        displayText.length > 18 -> 24.sp
        displayText.length > 12 -> 32.sp
        displayText.length > 8 -> 40.sp
        else -> 48.sp
    }

    val horizontalScroll = rememberScrollState()
    LaunchedEffect(displayText) {
        horizontalScroll.scrollTo(horizontalScroll.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Text(
            text = subText,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 22.dp)
                .testTag("sub_display"),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScroll)
                .animateContentSize(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = fontSize,
                    lineHeight = (fontSize.value * 1.15f).sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = if (uiState.isErrorDisplay) colors.allClearAccent else colors.textPrimary,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .testTag("expression_display"),
            )
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = HisaabTheme.colors
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.glassCard)
            .border(1.dp, colors.glassBorder, shape),
    ) {
        content()
    }
}

private fun mapHardwareKeyEvent(event: KeyEvent): CalcKey? {
    if (event.type != KeyEventType.KeyDown) return null
    if (event.isCtrlPressed || event.isMetaPressed || event.isAltPressed) return null

    return when (event.key) {
        Key.Zero, Key.NumPad0 -> CalcKey.ZERO
        Key.One, Key.NumPad1 -> CalcKey.ONE
        Key.Two, Key.NumPad2 -> CalcKey.TWO
        Key.Three, Key.NumPad3 -> CalcKey.THREE
        Key.Four, Key.NumPad4 -> CalcKey.FOUR
        Key.Five -> if (event.isShiftPressed) CalcKey.PERCENT else CalcKey.FIVE
        Key.NumPad5 -> CalcKey.FIVE
        Key.Six, Key.NumPad6 -> CalcKey.SIX
        Key.Seven, Key.NumPad7 -> CalcKey.SEVEN
        Key.Eight -> if (event.isShiftPressed) CalcKey.MULTIPLY else CalcKey.EIGHT
        Key.NumPad8 -> CalcKey.EIGHT
        Key.Nine, Key.NumPad9 -> CalcKey.NINE
        Key.Period, Key.NumPadDot -> CalcKey.DOT
        Key.Equals -> if (event.isShiftPressed) CalcKey.ADD else CalcKey.EQUALS
        Key.Plus, Key.NumPadAdd -> CalcKey.ADD
        Key.Minus, Key.NumPadSubtract -> CalcKey.SUBTRACT
        Key.Multiply, Key.NumPadMultiply, Key.X -> CalcKey.MULTIPLY
        Key.Slash, Key.NumPadDivide -> CalcKey.DIVIDE
        Key.Enter, Key.NumPadEnter, Key.NumPadEquals -> CalcKey.EQUALS
        Key.Backspace -> CalcKey.BS
        Key.Escape, Key.Delete -> CalcKey.AC
        else -> null
    }
}
