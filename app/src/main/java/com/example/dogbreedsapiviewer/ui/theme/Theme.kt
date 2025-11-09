package com.example.dogbreedsapiviewer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your app's color palette
private val DarkBlue = Color(0xFF0D253F)
private val LightBlue = Color(0xFF01B4E4)
private val LightGrey = Color(0xFFF0F0F0)
private val White = Color(0xFFFFFFFF)

// Define the ColorScheme for Dark Mode
private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = Color.Black,
    onBackground = White,
    onSurface = White
)

// Define the ColorScheme for Light Mode
private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    background = LightGrey,
    surface = White,
    onPrimary = White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun DogBreedsAPIViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography, // You can also define a custom Typography here
        content = content
    )
}
