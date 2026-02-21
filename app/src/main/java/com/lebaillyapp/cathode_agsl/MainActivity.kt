package com.lebaillyapp.cathode_agsl

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.lebaillyapp.cathode_agsl.ui.screen.CathodeComposition
import com.lebaillyapp.cathode_agsl.ui.screen.CathodeGalleryScreen
import com.lebaillyapp.cathode_agsl.ui.theme.CathodeAGSLTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  On installe le splash
        val splashScreen = installSplashScreen()
        // 3. La condition de maintien ( timer de 2s) // TEST ONLY !
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }
        lifecycleScope.launch {
            delay(800)
            isReady = true
        }
        // 4. L'animation de sortie (le zoom)
        splashScreen.setOnExitAnimationListener { splashScreenView ->

            // 1. On sépare l'icône du fond (View)
            val iconView = splashScreenView.iconView
            val backgroundView = splashScreenView.view // Le fond noir

            // 2. L'icône s'envole (Anticipate)
            val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 2f)
            val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 2f)
            val iconAlpha = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)

            // et il disparaît plus lentement pour couvrir l'UI
            val backAlpha = ObjectAnimator.ofFloat(backgroundView, View.ALPHA, 1f, 0f).apply {
                duration = 800L
                startDelay = 400L
            }

            AnimatorSet().apply {
                duration = 1000L
                interpolator = AnticipateInterpolator()
                playTogether(scaleX, scaleY, iconAlpha, backAlpha)
                doOnEnd { splashScreenView.remove() }
                start()
            }


        }
        enableEdgeToEdge()
        setContent {
            CathodeAGSLTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    //CathodeComposition(textToLoad = R.raw.demo_stress_test)
                    CathodeGalleryScreen()
                }
            }
        }
    }
}

