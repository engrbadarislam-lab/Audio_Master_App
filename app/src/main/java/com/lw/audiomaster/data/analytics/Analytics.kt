package com.lw.audiomaster.data.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Thin wrapper over Firebase Analytics. Safe to call before init (no-ops until [init]).
 * Screen views are logged from the nav graph; the canonical `purchase` event is fired
 * from BillingManager for every acknowledged Play purchase (deduped by token).
 */
object Analytics {
    private var fa: FirebaseAnalytics? = null
    private val loggedPurchaseTokens = HashSet<String>()

    fun init(context: Context) {
        if (fa == null) fa = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    fun screen(name: String) {
        fa?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, name)
        })
    }

    /** Canonical purchase event. [token] dedupes repeat deliveries of the same purchase. */
    fun purchase(token: String, productId: String, priceMicros: Long, currency: String) {
        if (token.isNotEmpty() && !loggedPurchaseTokens.add(token)) return
        fa?.logEvent(FirebaseAnalytics.Event.PURCHASE, Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, token)
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.CURRENCY, if (currency.isEmpty()) "USD" else currency)
            if (priceMicros > 0) putDouble(FirebaseAnalytics.Param.VALUE, priceMicros / 1_000_000.0)
        })
    }

    fun event(name: String, params: Bundle? = null) {
        fa?.logEvent(name, params)
    }
}
