package com.lw.audiomaster

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.lw.audiomaster.ads.AdManager
import com.lw.audiomaster.ads.AppOpenAdManager
import com.lw.audiomaster.data.billing.PremiumManager
import com.lw.audiomaster.di.AppContainer
import com.lw.audiomaster.di.ServiceLocator

class AudioMasterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val container = AppContainer(this)
        ServiceLocator.init(container)
        PremiumManager.init(this)
        AdManager.init(this)
        AppOpenAdManager.register(this)
        container.startBilling()



    }


}
