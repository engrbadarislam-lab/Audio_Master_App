package com.lw.audiomaster.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.lw.audiomaster.data.billing.PremiumManager
import java.lang.ref.WeakReference

object AppOpenAdManager : DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private const val TAG = "AppOpenAd"
    private const val SCREEN_KEY = "ads_app_open"
    private const val SLOW_NET_TIMEOUT_MS = 10_000L   // ← 10 sec max wait

    private var activityRef: WeakReference<Activity>? = null
    private val currentActivity: Activity?
        get() = activityRef?.get()
    private var coldStartConsumed = false
    private var showingAd = false
    private val handler = Handler(Looper.getMainLooper())

    @Volatile var welcomeBackRequested: Boolean = false
        private set

    @Volatile var appOpenAllowedHere: Boolean = false

    @Volatile var suppressNextResume: Boolean = false

    fun register(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // ── Network check ──
    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ── Foreground trigger ──
    override fun onStart(owner: LifecycleOwner) {
        if (!coldStartConsumed) { coldStartConsumed = true; return }
//        if (suppressNextResume) { suppressNextResume = false; return }
        if (PremiumManager.isPremium) return
        if (!AdConfig.isEnabled(SCREEN_KEY)) return
        if (showingAd) return
//        if (!appOpenAllowedHere) return
        welcomeBackRequested = true
    }

    fun consumeWelcomeBackRequest() { welcomeBackRequested = false }

    /**
     * Called by the Welcome Back overlay AFTER it's visible.
     * No internet → dismiss immediately.
     * Slow internet → 10s timeout then dismiss.
     */
    fun loadAndShow(onFinished: () -> Unit) {
        val activity = currentActivity
        if (activity == null || PremiumManager.isPremium ||
            !AdConfig.isEnabled(SCREEN_KEY)
        ) { onFinished(); return }

        // 1) No internet at all → skip silently, dismiss welcome back
        if (!isOnline(activity)) { onFinished(); return }

        // 2) Online → start 10s safety timer for slow network
        val timeout = Runnable {
            Log.d(TAG, "Slow internet timeout — giving up on app open")
            onFinished()
        }
        handler.postDelayed(timeout, SLOW_NET_TIMEOUT_MS)

        fun cleanup() = handler.removeCallbacks(timeout)

        AppOpenAd.load(
            activity, AdConfig.appOpenUnit(), AdManager.request(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    cleanup()
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            showingAd = false; onFinished()
                        }
                        override fun onAdFailedToShowFullScreenContent(e: AdError) {
                            showingAd = false; onFinished()
                        }
                        override fun onAdShowedFullScreenContent() {
                            showingAd = true
                        }
                    }
                    val act = currentActivity
                    if (act != null) ad.show(act) else onFinished()
                }

                override fun onAdFailedToLoad(err: LoadAdError) {
                    cleanup()
                    Log.d(TAG, "app open load failed: ${err.message}")
                    onFinished()
                }
            }
        )
    }

    // ── ActivityLifecycleCallbacks ──
    override fun onActivityStarted(activity: Activity) {
        if (!showingAd) activityRef = WeakReference(activity)
    }
    override fun onActivityResumed(activity: Activity) {
        if (!showingAd) activityRef = WeakReference(activity)
    }
    override fun onActivityCreated(a: Activity, b: Bundle?) {}
    override fun onActivityPaused(a: Activity) {}
    override fun onActivityStopped(a: Activity) {}
    override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
    override fun onActivityDestroyed(a: Activity) {
        if (currentActivity == a) activityRef = null
    }
}