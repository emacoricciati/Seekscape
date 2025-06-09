package it.polito.mad.lab5g10.seekscape.ui._theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimaryLight,
    onPrimary = OnOrangePrimaryLight,
    primaryContainer = OrangePrimaryLight.copy(alpha = 0.2f),
    onPrimaryContainer = OnOrangePrimaryLight,
    //inversePrimary = inversePrimary,
    secondary = GraySecondaryLight,
    //onSecondary = onSecondary,
    secondaryContainer = GraySecondaryLight.copy(alpha = 0.2f),
    onSecondaryContainer = BackgroundLight,
    //tertiary = tertiary,
    //onTertiary = onTertiary,
    //tertiaryContainer = tertiaryContainer,
    //onTertiaryContainer = onTertiaryContainer,
    background = BackgroundLight,
    onBackground = MainTextLight,
    surface = SurfaceLight,
    onSurface = MainTextLight,
    //surfaceVariant = surfaceVariant,
    //onSurfaceVariant = onSurfaceVariant,
    //surfaceTint = surfaceTint,
    //inverseSurface = inverseSurface,
    //inverseOnSurface = inverseOnSurface,
    error = ErrorColorLight,
    onError = OnErrorColorLight,
    //errorContainer = errorContainer,
    //onErrorContainer = onErrorContainer,
    outline = OutlineLight,
    //outlineVariant = outlineVariant,
    //scrim = scrim,
    //surfaceBright = surfaceBright,
    //surfaceContainer = surfaceContainer,
    //surfaceContainerHigh = surfaceContainerHigh,
    //surfaceContainerHighest = surfaceContainerHighest,
    //surfaceContainerLow = surfaceContainerLow,
    //surfaceContainerLowest = surfaceContainerLowest,
    //surfaceDim = surfaceDim,
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimaryDark,
    onPrimary = OnOrangePrimaryDark,
    primaryContainer = OrangePrimaryDark.copy(alpha = 0.2f),
    onPrimaryContainer = OnOrangePrimaryDark,
    //inversePrimary = inversePrimary,
    secondary = GraySecondaryDark,
    //onSecondary = onSecondary,
    secondaryContainer = GraySecondaryDark.copy(alpha = 0.2f),
    onSecondaryContainer = BackgroundDark,
    //tertiary = tertiary,
    //onTertiary = onTertiary,
    //tertiaryContainer = tertiaryContainer,
    //onTertiaryContainer = onTertiaryContainer,
    background = BackgroundDark,
    onBackground = MainTextDark,
    surface = SurfaceDark,
    onSurface = MainTextDark,
    //surfaceVariant = surfaceVariant,
    //onSurfaceVariant = onSurfaceVariant,
    //surfaceTint = surfaceTint,
    //inverseSurface = inverseSurface,
    //inverseOnSurface = inverseOnSurface,
    error = ErrorColorDark,
    onError = OnErrorColorDark,
    //errorContainer = errorContainer,
    //onErrorContainer = onErrorContainer,
    outline = OutlineDark,
    //outlineVariant = outlineVariant,
    //scrim = scrim,
    //surfaceBright = surfaceBright,
    //surfaceContainer = surfaceContainer,
    //surfaceContainerHigh = surfaceContainerHigh,
    //surfaceContainerHighest = surfaceContainerHighest,
    //surfaceContainerLow = surfaceContainerLow,
    //surfaceContainerLowest = surfaceContainerLowest,
    //surfaceDim = surfaceDim,
)

@Composable
fun SeekScapeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    /* dynamicColor: Boolean = true, */
    content: @Composable () -> Unit
) {
    /*
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme;

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}