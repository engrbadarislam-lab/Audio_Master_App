package com.lw.audiomaster.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lw.audiomaster.data.analytics.Analytics
import com.lw.audiomaster.di.ServiceLocator
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.screens.update.ForceUpdateScreen
import com.lw.audiomaster.ui.util.findActivity
import com.lw.audiomaster.ui.util.LocaleHelper
import com.lw.audiomaster.ui.screens.about.AboutScreen
import com.lw.audiomaster.ui.screens.detail.DetailScreen
import com.lw.audiomaster.ui.screens.editor.EditorScreen
import com.lw.audiomaster.ui.screens.equalizer.EqualizerScreen
import com.lw.audiomaster.ui.screens.export.ExportScreen
import com.lw.audiomaster.ui.screens.export.ExportSuccessScreen
import com.lw.audiomaster.ui.screens.help.HelpScreen
import com.lw.audiomaster.ui.screens.home.HomeScreen
import com.lw.audiomaster.ui.screens.importer.ImportScreen
import com.lw.audiomaster.ui.screens.language.LanguageScreen
import com.lw.audiomaster.ui.screens.language.LanguageSelectScreen
import com.lw.audiomaster.ui.screens.onboarding.OnboardingScreen
import com.lw.audiomaster.ui.screens.paywall.PaywallScreen
import com.lw.audiomaster.ui.screens.presets.PresetsScreen
import com.lw.audiomaster.ui.screens.settings.SettingsScreen
import com.lw.audiomaster.ui.screens.splash.SplashScreen
import com.lw.audiomaster.ui.screens.volume.VolumeScreen
import com.lw.audiomaster.ui.screens.library.LibraryScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val vm: AppViewModel = viewModel(factory = AppViewModel.Factory())
    val onboarded by vm.onboarded.collectAsState()
    val context = LocalContext.current
    val activity = context.findActivity()

    // Count meaningful navigations for the "every 3 clicks" interstitial.
    fun click() { activity?.let { ServiceLocator.container.registerClick(it) } }

    val updateRequired by ServiceLocator.container.forceUpdate.updateRequired.collectAsState()

    // Log a screen_view for every destination change.
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            destination.route?.let { Analytics.screen(it) }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    Box(Modifier.fillMaxSize()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                val dest = when {
                    !LocaleHelper.hasChosen(context) -> Routes.LANGUAGE_SELECT
                    !onboarded -> Routes.ONBOARDING
                    else -> Routes.HOME
                }
                navController.navigate(dest) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.LANGUAGE_SELECT) {
            LanguageSelectScreen(onChosen = {
                // The screen persists the locale and calls recreate(), after which Splash
                // re-routes to onboarding in the chosen language. This navigate is a fallback.
                navController.navigate(Routes.ONBOARDING) {
                    popUpTo(Routes.LANGUAGE_SELECT) { inclusive = true }
                }
            })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onDone = {
                vm.completeOnboarding()
                navController.navigate(Routes.PAYWALL) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }

        composable(Routes.PAYWALL) {
            PaywallScreen(vm = vm, onClose = {
                if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.PAYWALL) { inclusive = true }
                    }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                vm = vm,
                onNewProject = { click(); navController.navigate(Routes.IMPORT) },
                onQuickMaster = { click(); vm.loadDemoSource("Quick Master.wav"); navController.navigate(Routes.EDITOR) },
                onOpenPresets = { click(); vm.ensureSource(); navController.navigate(Routes.PRESETS) },
                onOpenEq = { click(); vm.ensureSource(); navController.navigate(Routes.EQUALIZER) },
                onOpenVolume = { click(); vm.ensureSource(); navController.navigate(Routes.VOLUME) },
                onOpenLibrary = { click(); navController.navigate(Routes.LIBRARY) },
                onOpenSettings = { click(); navController.navigate(Routes.SETTINGS) },
                onOpenProject = { id -> click(); navController.navigate(Routes.detail(id)) },
                onUpgrade = { navController.navigate(Routes.PAYWALL) }
            )
        }

        composable(Routes.IMPORT) {
            ImportScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onLoaded = {
                    navController.navigate(Routes.EDITOR) {
                        popUpTo(Routes.IMPORT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EDITOR) {
            EditorScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onOpenPresets = { navController.navigate(Routes.PRESETS) },
                onOpenEq = { navController.navigate(Routes.EQUALIZER) },
                onOpenVolume = { navController.navigate(Routes.VOLUME) },
                onExport = { navController.navigate(Routes.EXPORT) }
            )
        }

        composable(Routes.PRESETS) {
            PresetsScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onUpgrade = { navController.navigate(Routes.PAYWALL) },
                onApplied = { navController.popBackStack() }
            )
        }

        composable(Routes.EQUALIZER) {
            EqualizerScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() }
            )
        }

        composable(Routes.VOLUME) {
            VolumeScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onUpgrade = { navController.navigate(Routes.PAYWALL) },
                onDone = { navController.popBackStack() }
            )
        }

        composable(Routes.EXPORT) {
            ExportScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onUpgrade = { navController.navigate(Routes.PAYWALL) },
                onExported = {
                    navController.navigate(Routes.EXPORT_SUCCESS) {
                        popUpTo(Routes.EXPORT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EXPORT_SUCCESS) {
            ExportSuccessScreen(
                vm = vm,
                onDone = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onOpenLibrary = {
                    navController.navigate(Routes.LIBRARY) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.LIBRARY) {
            LibraryScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onNewProject = { navController.navigate(Routes.IMPORT) },
                onOpenProject = { id -> navController.navigate(Routes.detail(id)) }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            DetailScreen(vm = vm, projectId = id, onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onUpgrade = { navController.navigate(Routes.PAYWALL) },
                onLanguage = { navController.navigate(Routes.LANGUAGE) },
                onAbout = { navController.navigate(Routes.ABOUT) },
                onHelp = { navController.navigate(Routes.HELP) }
            )
        }

        composable(Routes.LANGUAGE) { LanguageScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.HELP) { HelpScreen(onBack = { navController.popBackStack() }) }
    }

        if (updateRequired) ForceUpdateScreen()
    }
}
