package com.lebaillyapp.cathode_agsl.ui.screen

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.zIndex
import com.lebaillyapp.cathode_agsl.R
import com.lebaillyapp.cathode_agsl.model.CrtSettings
import com.lebaillyapp.cathode_agsl.model.updateCrtUniforms
import com.lebaillyapp.cathode_agsl.ui.component.CrtControlPanel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


@Composable
fun CathodeMonoPictureComposition(
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current

    /**
     * RuntimeShader Initialization.
     * Loads the 'crt_lens' AGSL source responsible for the global physical
     * simulation (Fisheye curvature, Phosphor grid, and Signal noise).
     */
    val shaderSource = remember {
        resources.openRawResource(R.raw.crt_lens).use { it.bufferedReader().readText() }
    }
    val runtimeShader = remember(shaderSource) { RuntimeShader(shaderSource) }

    /**
     * Global Animation Time.
     * Drives the temporal artifacts of the CRT shader, such as screen flicker,
     * rolling scanlines, and intermittent signal jitter.
     */
    val totalTime by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing))
    )

    // UI and Shader state management
    var settings by remember { mutableStateOf(CrtSettings()) }
    var showSettings by remember { mutableStateOf(false) }


    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {

        // --- DISTORTED CRT RENDER BOX ---
        /**
         * The graphicsLayer applies the RenderEffect to this specific container.
         * Everything inside (LazyColumn and its children) will undergo the
         * spherical distortion and color shifts defined in the AGSL shader.
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Update AGSL uniforms for current frame
                    runtimeShader.setFloatUniform("size", size.width, size.height)
                    runtimeShader.setFloatUniform("time", totalTime)
                    runtimeShader.updateCrtUniforms(settings)

                    // Create and apply the Compose RenderEffect
                    renderEffect = RenderEffect.createRuntimeShaderEffect(
                        runtimeShader, "composable"
                    ).asComposeRenderEffect()
                }
                .clickable { showSettings = !showSettings }
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_demo_8),
                contentDescription = "demo Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // --- NON-DISTORTED CONTROL PANEL ---
        /**
         * Placed outside the shader-affected Box to ensure the control
         * interface remains sharp and legible for the user.
         */
        if (showSettings) {
            CrtControlPanel(
                settings = settings,
                onSettingsChange = { settings = it },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .zIndex(1f)
            )
        }
    }
}