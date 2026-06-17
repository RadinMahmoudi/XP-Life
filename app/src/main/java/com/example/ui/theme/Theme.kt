package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricIndigo,
    secondary = RetroCyan,
    tertiary = XPBlue,
    background = DeepSpaceBackground,
    surface = SlateCardBackground,
    onPrimary = TextWhite,
    onSecondary = DeepSpaceBackground,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = Pink40,
    tertiary = XPBlue,
    background = TextWhite,
    surface = PurpleGrey40,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = DeepSpaceBackground,
    onSurface = DeepSpaceBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Gamified app default to darkTheme
    dynamicColor: Boolean = false, // Disable dynamic colors so our customized RPG palette shines
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
