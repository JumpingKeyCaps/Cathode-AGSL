package com.lebaillyapp.cathode_agsl.ui.screen

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lebaillyapp.cathode_agsl.R
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.zIndex
import com.lebaillyapp.cathode_agsl.model.CrtSettings
import com.lebaillyapp.cathode_agsl.model.updateCrtUniforms
import com.lebaillyapp.cathode_agsl.ui.component.CrtControlPanel
import com.lebaillyapp.cathode_agsl.ui.component.LaserTextDisplay

/**
 * # Main Orchestrator for the Cathode CRT Simulation.
 * * This screen assembles the AGSL-distorted content and the clean UI overlay.
 * It manages the global animation time, shader loading, and coordinates the
 * feedback loop between the text reveal and the auto-scrolling behavior.
 *
 * @param modifier Modifier applied to the root container.
 * @param textToLoad Raw resource ID of the text file to be displayed in the terminal.
 */
@Composable
fun CathodeComposition(
    modifier: Modifier = Modifier,
    textToLoad: Int = R.raw.demo_long_manual
) {
    val context = LocalContext.current
    val resources = LocalResources.current

    /**
     * Shader Resource Management.
     * Loads the master 'crt_lens' AGSL source which provides the global
     * post-processing (Fisheye, Scanlines, Jitter).
     */
    val shaderSource = remember {
        resources.openRawResource(R.raw.crt_lens).use { it.bufferedReader().readText() }
    }
    val runtimeShader = remember(shaderSource) { RuntimeShader(shaderSource) }

    /**
     * Text Content Loading.
     * Asynchronously reads the raw text file to prevent UI blocking during initialization.
     */
    val demoText by produceState(initialValue = "") {
        value = resources.openRawResource(textToLoad).use { it.bufferedReader().readText() }
    }

    /**
     * Global Temporal Reference.
     * An infinite float transition used to drive flicker, jitter, and wave effects in the shader.
     */
    val totalTime by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing))
    )

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Screen-level state for CRT tuning and UI visibility
    var settings by remember { mutableStateOf(CrtSettings()) }
    var showSettings by remember { mutableStateOf(false) }

    // Root container holding both the distorted world and the clean UI
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {

        // --- 1. DISTORTED CRT RENDER LAYER ---
        /**
         * This Box acts as the 'Canvas' for the AGSL Shader.
         * The graphicsLayer is used to inject the RuntimeShader as a RenderEffect,
         * effectively distorting every child component (Column, Text, etc.).
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Injecting standard and custom uniforms into the AGSL pipeline
                    runtimeShader.setFloatUniform("size", size.width, size.height)
                    runtimeShader.setFloatUniform("time", totalTime)
                    runtimeShader.updateCrtUniforms(settings)

                    // Converting Android RuntimeShader to Compose RenderEffect
                    renderEffect = RenderEffect.createRuntimeShaderEffect(
                        runtimeShader, "composable"
                    ).asComposeRenderEffect()
                }
                .clickable { showSettings = !showSettings }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 40.dp)
            ) {
                if (demoText.isNotEmpty()) {
                    /**
                     * Inner Component: The Laser Terminal Text.
                     * It internally applies its own local 'reveal' shader before
                     * being processed by the parent's 'CRT' shader.
                     */
                    LaserTextDisplay(
                        fullText = demoText,
                        onCharAdded = {
                            // Automatically scroll to bottom as new lines are "typed"
                            scope.launch { scrollState.scrollTo(scrollState.maxValue) }
                        },
                        charDelay = 20L
                    )
                }
                Spacer(modifier = Modifier.height(150.dp))
            }
        }

        // --- 2. CONTROL OVERLAY (CLEAN LAYER) ---
        /**
         * This section is placed OUTSIDE the distorted Box to remain perfectly sharp.
         * It allows the user to interact with the shader parameters without the UI
         * itself being warped or blurred.
         */
        if (showSettings) {
            CrtControlPanel(
                settings = settings,
                onSettingsChange = { settings = it },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .zIndex(1f) // Ensures the panel sits above the distorted content
            )
        }
    }
}