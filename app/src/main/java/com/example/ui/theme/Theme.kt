package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Explicit Light Color Scheme with Yellow Accent as requested
private val LightColorScheme = lightColorScheme(
  primary = GsquadYellow,
  onPrimary = GsquadOnYellow,
  primaryContainer = GsquadYellowContainer,
  onPrimaryContainer = GsquadOnYellow,
  secondary = DerivDark,
  onSecondary = Color.White,
  secondaryContainer = LightSurfaceSecondary,
  onSecondaryContainer = TextPrimary,
  tertiary = TradeGreen,
  onTertiary = Color.White,
  tertiaryContainer = TradeGreenBg,
  onTertiaryContainer = TradeGreen,
  background = LightBackground,
  onBackground = TextPrimary,
  surface = LightSurface,
  onSurface = TextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = LightBorder,
  outlineVariant = LightBorderSubtle,
  error = TradeRed,
  onError = Color.White,
  errorContainer = TradeRedBg,
  onErrorContainer = TradeRed
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  // We lock dynamicColor to false to maintain the user's requested light background & yellow accent
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LightColorScheme,
    typography = Typography,
    content = content
  )
}
