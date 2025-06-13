package it.polito.mad.lab5g10.seekscape.ui._theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import it.polito.mad.lab5g10.seekscape.models.AppState
import com.google.accompanist.systemuicontroller.rememberSystemUiController


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
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val darkSystemTheme: Boolean = isSystemInDarkTheme()
    val isDarkMode = AppState.isDarkMode.collectAsState().value

    val colorScheme =
        if ((isDarkMode==null && darkSystemTheme) || (isDarkMode!=null && isDarkMode))
            DarkColorScheme
        else
            LightColorScheme;
    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorScheme.background,
            darkIcons = !((isDarkMode == null && darkSystemTheme) || (isDarkMode != null && isDarkMode))
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}