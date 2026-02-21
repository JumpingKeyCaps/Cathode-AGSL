package com.lebaillyapp.cathode_agsl.model

import android.graphics.RuntimeShader

/**
 * Configuration data class for CRT (Cathode Ray Tube) simulation parameters.
 * Holds all values required by the AGSL shader to replicate analog screen artifacts,
 * from physical lens distortion to signal instability (glitch) effects.
 *
 * @property FISH_EYE_STRENGTH Controls the spherical curvature of the screen.
 * @property SCREEN_ZOOM Adjusts the scale to compensate for corner cropping caused by distortion.
 * @property VIGNETTE_INTENSITY Darkens the edges of the screen to simulate tube corners.
 * @property GRID_OPACITY Transparency of the underlying RGB phosphor grid.
 * @property GRID_SPACING Size of the phosphor grid cells.
 * @property SCANLINE_DENSITY Frequency of the horizontal scanlines.
 * @property SCANLINE_OPACITY Intensity of the scanline darkening effect.
 * @property TEXT_ANAGLYPH Horizontal offset for chromatic aberration on the text layer.
 * @property GRID_ANAGLYPH Horizontal offset for chromatic aberration on the phosphor grid.
 * @property SIGNAL_SHIFT Constant horizontal displacement of the image.
 * @property JITTER_CHANCE Probability of a scanline experiencing horizontal drift.
 * @property JITTER_INTENSITY Magnitude of the individual line horizontal drift.
 * @property GLOBAL_JITTER_CHANCE Probability of the entire screen shaking.
 * @property GLOBAL_JITTER_STRENGTH Magnitude of the full-screen shake.
 */
data class CrtSettings(
    // Physical hardware simulation parameters
    val FISH_EYE_STRENGTH: Float = 0.90f,
    val SCREEN_ZOOM: Float = 1.40f,
    val VIGNETTE_INTENSITY: Float = 0.90f,
    val GRID_OPACITY: Float = 0.05f,
    val GRID_SPACING: Float = 80.0f,
    val SCANLINE_DENSITY: Float = 0.50f,
    val SCANLINE_OPACITY: Float = 0.05f,

    // Signal interference and glitch parameters
    val TEXT_ANAGLYPH: Float = 4.0f,
    val GRID_ANAGLYPH: Float = 6.0f,
    val SIGNAL_SHIFT: Float = 15.0f,
    val JITTER_CHANCE: Float = 0.01f,
    val JITTER_INTENSITY: Float = 45.0f,
    val GLOBAL_JITTER_CHANCE: Float = 0.005f,
    val GLOBAL_JITTER_STRENGTH: Float = 25.0f
)

/**
 * Extension function to bridge the [CrtSettings] data class with the AGSL RuntimeShader.
 * Maps each property of the settings object to its corresponding uniform defined in the shader code.
 *
 * @param settings The configuration object containing the values to be applied.
 */
fun RuntimeShader.updateCrtUniforms(settings: CrtSettings) {
    // Apply physical simulation uniforms
    setFloatUniform("FISH_EYE_STRENGTH", settings.FISH_EYE_STRENGTH)
    setFloatUniform("SCREEN_ZOOM", settings.SCREEN_ZOOM)
    setFloatUniform("VIGNETTE_INTENSITY", settings.VIGNETTE_INTENSITY)
    setFloatUniform("GRID_OPACITY", settings.GRID_OPACITY)
    setFloatUniform("GRID_SPACING", settings.GRID_SPACING)
    setFloatUniform("SCANLINE_DENSITY", settings.SCANLINE_DENSITY)
    setFloatUniform("SCANLINE_OPACITY", settings.SCANLINE_OPACITY)

    // Apply glitch and signal uniforms
    setFloatUniform("TEXT_ANAGLYPH", settings.TEXT_ANAGLYPH)
    setFloatUniform("GRID_ANAGLYPH", settings.GRID_ANAGLYPH)
    setFloatUniform("SIGNAL_SHIFT", settings.SIGNAL_SHIFT)
    setFloatUniform("JITTER_CHANCE", settings.JITTER_CHANCE)
    setFloatUniform("JITTER_INTENSITY", settings.JITTER_INTENSITY)
    setFloatUniform("GLOBAL_JITTER_CHANCE", settings.GLOBAL_JITTER_CHANCE)
    setFloatUniform("GLOBAL_JITTER_STRENGTH", settings.GLOBAL_JITTER_STRENGTH)
}