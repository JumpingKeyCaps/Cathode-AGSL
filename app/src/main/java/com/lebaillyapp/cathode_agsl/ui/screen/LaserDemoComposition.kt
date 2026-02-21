package com.lebaillyapp.cathode_agsl.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lebaillyapp.cathode_agsl.ui.component.LaserTextDisplay

@Composable
fun LaserDemoComposition() {
    var aiResponse by remember { mutableStateOf("") }
    // Clé pour forcer la recomposition
    var refreshKey by remember { mutableIntStateOf(0) }

    val sampleText = "Walking through an old-growth forest is like entering a library where the books have been replaced by trees. " +
            "At first glance, it looks like absolute chaos. Dead logs lie rotting on the damp ground, branches tangle like uncombed hair, and ivy climbs skin-like over every trunk. It feels like a room that hasn't been cleaned in a thousand years."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En utilisant key(), on force Compose à détruire et recréer le composant
        // quand refreshKey change, ce qui relance tous les LaunchedEffect.
        key(refreshKey) {
            if (aiResponse.isNotEmpty()) {
                LaserTextDisplay(
                    fullText = aiResponse,
                    fontSize = 18.sp,
                    charDelay = 30L
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                aiResponse = sampleText
                refreshKey++ // On incrémente pour reset l'animation
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(".", color = Color.White)
        }
    }
}