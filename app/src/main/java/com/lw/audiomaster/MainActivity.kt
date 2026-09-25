package com.lw.audiomaster

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lw.audiomaster.ads.AdClickLoadingHost
import com.lw.audiomaster.ads.WelcomeBackHost
import com.lw.audiomaster.di.ServiceLocator
import com.lw.audiomaster.ui.navigation.AppNavHost
import com.lw.audiomaster.ui.theme.AudioMasterTheme
import com.lw.audiomaster.ui.util.LocaleHelper

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioMasterTheme {
                WelcomeBackHost {
                    AppNavHost()
                    AdClickLoadingHost()     // ← draws the loading dialog for every click ad


                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        // Refresh entitlements in case a purchase completed elsewhere.
        runCatching { ServiceLocator.container.billingManager.refreshPurchases() }
    }
}
