package com.lw.audiomaster.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val LANGUAGE_SELECT = "language_select"
    const val ONBOARDING = "onboarding"
    const val PAYWALL = "paywall"
    const val HOME = "home"
    const val IMPORT = "import"
    const val EDITOR = "editor"
    const val PRESETS = "presets"
    const val EQUALIZER = "equalizer"
    const val VOLUME = "volume"
    const val EXPORT = "export"
    const val EXPORT_SUCCESS = "export_success"
    const val LIBRARY = "library"
    const val DETAIL = "detail/{id}"
    const val SETTINGS = "settings"
    const val LANGUAGE = "language"
    const val ABOUT = "about"
    const val HELP = "help"

    fun detail(id: Long) = "detail/$id"
}
