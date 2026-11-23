package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Core Cosmic Midnight Palette (Hisaab Kitaab)
val MidnightBg = Color(0xFF0C0A1D)
val MidnightSurface = Color(0xFF15122C)
val MidnightSheet = Color(0xFF16132E)

val GlowIndigo = Color(0xFF5B3DF5)
val GlowAmber = Color(0xFFFF7A1A)

val GlassFill = Color(0x14FFFFFF) // ~8% translucent glass
val GlassCardFill = Color(0x12FFFFFF) // ~7% translucent card
val GlassBorder = Color(0x24FFFFFF) // ~14% hairline border
val GlassHoverFill = Color(0x26FFFFFF)

val TextPrimary = Color(0xFFF4F1FF)
val TextMuted = Color(0xFF9A94C4)

val OperatorAmber = Color(0xFFFF9F1C)
val OperatorAmberPressed = Color(0xFFFFB347)
val OperatorTextDark = Color(0xFF1B1100)

val EqualsGold = Color(0xFFFFD166)
val EqualsAmber = Color(0xFFFF9F1C)

val AccentCoral = Color(0xFFFF5D73)

// Reaction Banner Palettes
val NegGradStart = Color(0x47FF463C) // rgba(255,70,60,.28)
val NegGradEnd = Color(0x38FF8C28) // rgba(255,140,40,.22)
val NegBorder = Color(0xFFFF6B4A)
val NegText = Color(0xFFFFD5C9)

val PosGradStart = Color(0x4728C878) // rgba(40,200,120,.28)
val PosGradEnd = Color(0x38FFC83C) // rgba(255,200,60,.22)
val PosBorder = Color(0xFF3DDC97)
val PosText = Color(0xFFE5FFD9)

val ZeroGradStart = Color(0x405A82FF) // rgba(90,130,255,.25)
val ZeroGradEnd = Color(0x388C96AA) // rgba(140,150,170,.22)
val ZeroBorder = Color(0xFF8AA4FF)
val ZeroText = Color(0xFFDBE4FF)

val ErrFill = Color(0x33FF5D73) // rgba(255,93,115,.20)
val ErrBorder = Color(0xFFFF5D73)
val ErrText = Color(0xFFFFD0D6)

@Immutable
data class HisaabColors(
    val canvasBackground: Color = MidnightBg,
    val sheetBackground: Color = MidnightSheet,
    val glowPrimary: Color = GlowIndigo,
    val glowSecondary: Color = GlowAmber,
    val glassCard: Color = GlassCardFill,
    val glassKey: Color = GlassFill,
    val glassBorder: Color = GlassBorder,
    val textPrimary: Color = TextPrimary,
    val textMuted: Color = TextMuted,
    val operatorBg: Color = OperatorAmber,
    val operatorText: Color = OperatorTextDark,
    val equalsStart: Color = EqualsGold,
    val equalsEnd: Color = EqualsAmber,
    val allClearAccent: Color = AccentCoral,
    val negStart: Color = NegGradStart,
    val negEnd: Color = NegGradEnd,
    val negBorder: Color = NegBorder,
    val negText: Color = NegText,
    val posStart: Color = PosGradStart,
    val posEnd: Color = PosGradEnd,
    val posBorder: Color = PosBorder,
    val posText: Color = PosText,
    val zeroStart: Color = ZeroGradStart,
    val zeroEnd: Color = ZeroGradEnd,
    val zeroBorder: Color = ZeroBorder,
    val zeroText: Color = ZeroText,
    val errBg: Color = ErrFill,
    val errBorder: Color = ErrBorder,
    val errText: Color = ErrText,
)
