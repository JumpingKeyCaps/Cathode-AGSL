package com.lebaillyapp.cathode_agsl.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lebaillyapp.cathode_agsl.model.CrtSettings

/**
 * # CRT Configuration Control Panel.
 *
 * This UI component provides a side-bar interface to manipulate the [CrtSettings]
 * in real-time. It uses a series of sliders to tune the AGSL shader uniforms,
 * categorized by effect type (Optics, Phosphors, Signal, and Stability).
 *
 * @param settings The current state of the CRT configuration.
 * @param onSettingsChange Callback triggered whenever a setting is adjusted, providing an updated [CrtSettings] object.
 * @param modifier The modifier to be applied to the panel container.
 */
@Composable
fun CrtControlPanel(
    settings: CrtSettings,
    onSettingsChange: (CrtSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(210.dp)
            .fillMaxHeight()
            .background(Color.Black.copy(alpha = 0.4f)) // Semi-transparent for better legibility over the shader
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // --- SECTION: OPTICS & GEOMETRY ---
        Text("OPTICS & GEOMETRY", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

        SettingSlider("Fish Eye", settings.FISH_EYE_STRENGTH, -2.0f, 2.0f) {
            onSettingsChange(settings.copy(FISH_EYE_STRENGTH = it))
        }
        SettingSlider("Zoom", settings.SCREEN_ZOOM, 0.0f, 2.5f) {
            onSettingsChange(settings.copy(SCREEN_ZOOM = it))
        }
        SettingSlider("Vignette", settings.VIGNETTE_INTENSITY, 0f, 2.0f) {
            onSettingsChange(settings.copy(VIGNETTE_INTENSITY = it))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- SECTION: PHOSPHOR & GRID ---
        Text("PHOSPHOR & GRID", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

        SettingSlider("Grid Opacity", settings.GRID_OPACITY, 0f, 1.0f) {
            onSettingsChange(settings.copy(GRID_OPACITY = it))
        }
        SettingSlider("Grid Spacing", settings.GRID_SPACING, 10f, 400f) {
            onSettingsChange(settings.copy(GRID_SPACING = it))
        }
        SettingSlider("Scanline Density", settings.SCANLINE_DENSITY, 0f, 50f) {
            onSettingsChange(settings.copy(SCANLINE_DENSITY = it))
        }
        SettingSlider("Scanline Opacity", settings.SCANLINE_OPACITY, 0f, 0.5f) {
            onSettingsChange(settings.copy(SCANLINE_OPACITY = it))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- SECTION: SIGNAL GLITCH ---
        Text("SIGNAL GLITCH", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

        SettingSlider("Text Anaglyph", settings.TEXT_ANAGLYPH, 0f, 50f) {
            onSettingsChange(settings.copy(TEXT_ANAGLYPH = it))
        }
        SettingSlider("Grid Anaglyph", settings.GRID_ANAGLYPH, 0f, 50f) {
            onSettingsChange(settings.copy(GRID_ANAGLYPH = it))
        }
        SettingSlider("Signal Shift", settings.SIGNAL_SHIFT, -100f, 100f) {
            onSettingsChange(settings.copy(SIGNAL_SHIFT = it))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- SECTION: TEMPORAL INSTABILITY ---
        Text("TEMPORAL INSTABILITY", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

        SettingSlider("Jitter Chance", settings.JITTER_CHANCE, 0f, 0.9f) {
            onSettingsChange(settings.copy(JITTER_CHANCE = it))
        }
        SettingSlider("Jitter Intensity", settings.JITTER_INTENSITY, 0f, 200f) {
            onSettingsChange(settings.copy(JITTER_INTENSITY = it))
        }
        SettingSlider("Global Jitter Chance", settings.GLOBAL_JITTER_CHANCE, 0f, 0.9f) {
            onSettingsChange(settings.copy(GLOBAL_JITTER_CHANCE = it))
        }
        SettingSlider("Global Jitter Power", settings.GLOBAL_JITTER_STRENGTH, 0f, 150f) {
            onSettingsChange(settings.copy(GLOBAL_JITTER_STRENGTH = it))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * Custom Slider Row for individual settings.
 * Displays a label, the current formatted value, and a Slider for input.
 *
 * @param label The display name of the setting.
 * @param value The current float value.
 * @param min The minimum range of the slider.
 * @param max The maximum range of the slider.
 * @param onValueChange Callback to propagate value updates.
 */
@Composable
fun SettingSlider(label: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
            Text("%.2f".format(value), color = Color.White, style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            modifier = Modifier.height(24.dp)
        )
    }
}