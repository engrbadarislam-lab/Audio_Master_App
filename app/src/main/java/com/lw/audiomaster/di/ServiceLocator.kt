package com.lw.audiomaster.di

object ServiceLocator {
    lateinit var container: AppContainer
        private set

    fun init(container: AppContainer) {
        this.container = container
    }
}
