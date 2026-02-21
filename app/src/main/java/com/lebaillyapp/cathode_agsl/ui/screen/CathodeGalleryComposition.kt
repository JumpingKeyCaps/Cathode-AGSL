package com.lebaillyapp.cathode_agsl.ui.screen

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lebaillyapp.cathode_agsl.R
import com.lebaillyapp.cathode_agsl.model.CrtSettings
import com.lebaillyapp.cathode_agsl.model.updateCrtUniforms
import com.lebaillyapp.cathode_agsl.ui.component.CrtControlPanel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

/**
 * # CRT Gallery Screen Component.
 * * This screen showcases a scrolling list of images processed through a
 * Cathode Ray Tube simulation shader. It demonstrates how standard Compose
 * layouts like LazyColumn can be completely transformed into a retro-analog
 * visual experience using AGSL post-processing.
 */
@Composable
fun CathodeGalleryScreen(
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

    /**
     * Hardcoded Asset List.
     * A collection of local drawable resources to be displayed in the gallery.
     */
    val imageList = remember {
        listOf(
            R.drawable.img_demo_1,
            R.drawable.img_demo_2,
            R.drawable.img_demo_3,
            R.drawable.img_demo_4,
            R.drawable.img_demo_5,
            R.drawable.img_demo_6,
            R.drawable.img_demo_8,
            R.drawable.img_demo_7
        )
    }

    val listState = rememberLazyListState()

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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Iterating through the image list to render gallery items
                itemsIndexed(imageList) { index, imageRes ->
                    GalleryItem(
                        imageRes = imageRes,
                        index = index
                    )
                }

                // Bottom spacer to ensure the last item is not hidden by UI elements
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
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

/**
 * ### Individual Gallery Item Component.
 * Renders a local image within a stylized container.
 * Note: While the component itself is standard, its final appearance is
 * dictated by the parent's AGSL shader (Chromatic aberration, Scanlines).
 *
 * @param imageRes The drawable resource ID for the image.
 * @param index The position of the item in the list.
 */
@Composable
fun GalleryItem(
    imageRes: Int,
    index: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f)) // Subtle highlight to define item area
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Archive Image $index",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}