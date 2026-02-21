package com.lebaillyapp.cathode_agsl.ui.component

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.lebaillyapp.cathode_agsl.R
import androidx.compose.ui.platform.LocalResources

/**
 * # Laser Terminal Text Display Component.
 * * This component simulates a sequential character-by-character text reveal
 * synchronized with an AGSL-powered laser sweep effect. It features automatic
 * syntax highlighting based on a predefined keyword map.
 *
 * @param fullText The raw source text to be displayed sequentially.
 * @param fontSize The typography size for the rendered text.
 * @param charDelay Milliseconds to wait between each character reveal.
 * @param onCharAdded Callback triggered every time a new character is rendered.
 */
@Composable
fun LaserTextDisplay(
    fullText: String,
    fontSize: TextUnit = 13.sp,
    charDelay: Long = 40L,
    onCharAdded: () -> Unit = {}
) {
    val contextRes = LocalResources.current

    /**
     * RuntimeShader initialization.
     * Loads the 'text_reveal_optical' AGSL source to handle real-time
     * light distortion and chromatic aberration at the cursor position.
     */
    val shaderSource = remember {
        contextRes.openRawResource(R.raw.text_reveal_optical).use { it.bufferedReader().readText() }
    }
    val runtimeShader = remember(shaderSource) { RuntimeShader(shaderSource) }

    // State management for laser tracking and text progression
    var lastCharPos by remember { mutableStateOf(Offset.Zero) }
    var displayedCharCount by remember { mutableIntStateOf(0) }
    var isWriting by remember { mutableStateOf(true) }

    // --- 1. HIGHLIGHTING CONFIGURATION ---
    /**
     * Keyword-to-Color mapping for the syntax highlighter.
     * Used to generate the AnnotatedString styles dynamically.
     */
    val highlights = remember {
        mapOf(
            // Status Tags
            "[  OK  ]" to Color(0xFF00FF88),   // Neon Green
            "[ WARN ]" to Color(0xFFFFBB00),  // Amber
            "[ INFO ]" to Color(0xFF00CCFF),  // Cyan
            "[SYSTEM]" to Color(0xFFFFFFFF),  // Pure White
            "[ DIAG ]" to Color(0xFFBB88FF),  // Electric Purple
            "[ TEST ]" to Color(0xFFFFEEAA),  // Pale Yellow
            "[ DUMP ]" to Color(0xFF777777),  // Stealth Gray
            "[ RUN  ]" to Color(0xFF00FF88),  // Active Green
            "[ WELCOME ]" to Color(0xFFFF3377), // Laser Pink

            // Structural Elements
            "-----------------------------------------------------------" to Color(0xFFF50057),
            "root@retroscreen" to Color(0xFFC6FF00),
            "RETRO CRT BABY" to Color(0xFFFAA02A),
            "#########[" to Color(0xFF7A18DC),
            "]#########" to Color(0xFF7217CE),

            // Technical Values
            "0x" to Color(0xFFFFCC00),         // Gold Memory Addresses
            "120Hz" to Color(0xFFD500F9),      // Purple High-Speed
            "ERROR" to Color(0xFFFF3377),      // Error Rose
        )
    }

    // --- 2. HIGHLIGHTING LOGIC ---
    /**
     * Builds an AnnotatedString based on the current progression of [displayedCharCount].
     * Scans the visible slice of text to apply styles from the [highlights] map.
     */
    val annotatedText = remember(displayedCharCount, fullText) {
        val currentRawText = fullText.take(displayedCharCount)
        buildAnnotatedString {
            append(currentRawText)
            highlights.forEach { (key, color) ->
                var index = currentRawText.indexOf(key)
                while (index != -1) {
                    addStyle(
                        style = SpanStyle(color = color, fontWeight = FontWeight.Bold),
                        start = index,
                        end = index + key.length
                    )
                    index = currentRawText.indexOf(key, index + 1)
                }
            }
        }
    }

    val cyberFont = FontFamily(Font(R.font.pixel_font_regular, FontWeight.Normal))

    // Shader uniform animations
    val laserIntensity by animateFloatAsState(
        targetValue = if (isWriting) 1f else 0f,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label = "LaserIntensity"
    )

    val time by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing))
    )

    /**
     * Typing Effect Orchestrator.
     * Manages the coroutine loop to increment visible characters over time.
     */
    LaunchedEffect(fullText) {
        isWriting = true
        displayedCharCount = 0
        for (i in 1..fullText.length) {
            delay(charDelay)
            displayedCharCount = i
            onCharAdded()
        }
        delay(150)
        isWriting = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .graphicsLayer {
                // Binding AGSL uniforms to the graphics layer
                runtimeShader.setFloatUniform("cursor", lastCharPos.x, lastCharPos.y)
                runtimeShader.setFloatUniform("radius", fontSize.toPx() * 1.2f)
                runtimeShader.setFloatUniform("time", time)
                runtimeShader.setFloatUniform("laserIntensity", laserIntensity)

                renderEffect = RenderEffect.createRuntimeShaderEffect(
                    runtimeShader, "composable"
                ).asComposeRenderEffect()
            }
    ) {
        Text(
            text = annotatedText,
            style = LocalTextStyle.current.copy(
                fontFamily = cyberFont,
                fontSize = fontSize,
                lineHeight = fontSize * 1.4f,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = 0.9f)
            ),
            /**
             * Text Layout Callback.
             * Calculates the precise X/Y screen coordinates of the last revealed character.
             * This Offset is passed to the AGSL shader to center the laser point.
             */
            onTextLayout = { textLayoutResult ->
                if (displayedCharCount > 0) {
                    val lastIndex = (displayedCharCount - 1).coerceIn(0, fullText.length - 1)
                    val rect = textLayoutResult.getCursorRect(lastIndex)
                    if (rect.center.x > 0) lastCharPos = rect.center
                }
            }
        )
    }
}

