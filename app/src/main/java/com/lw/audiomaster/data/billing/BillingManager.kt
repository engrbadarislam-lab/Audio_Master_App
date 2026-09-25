package com.lw.audiomaster.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.lw.audiomaster.data.analytics.Analytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Central Play Billing wrapper.
 * IMPORTANT: SUBS and INAPP are queried in separate calls — mixing types throws
 * IllegalArgumentException.
 *
 * Premium flag: PremiumManager is the ONE flag. A purchase/restore sets it,
 * so ads stop immediately and the value is saved in prefs.
 */
class BillingManager(
    context: Context,
    private val onProChanged: (Boolean) -> Unit
) {
    private val appContext = context.applicationContext

    init { PremiumManager.init(appContext) }

    private val _state = MutableStateFlow(PurchaseState.Idle)
    val state: StateFlow<PurchaseState> = _state.asStateFlow()

    private val _plans = MutableStateFlow<List<PlanOption>>(emptyList())
    val plans: StateFlow<List<PlanOption>> = _plans.asStateFlow()

    /** Same flag the ads use. */
    val isPro: StateFlow<Boolean> = PremiumManager.isPremiumFlow

    private val subDetails = mutableMapOf<String, ProductDetails>()
    private val inAppDetails = mutableMapOf<String, ProductDetails>()

    private val purchasesListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { handlePurchase(it) }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _state.value = PurchaseState.Ready
        } else {
            _state.value = PurchaseState.Error
        }
    }

    private val client: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(purchasesListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun start() {
        if (client.isReady) {
            queryEverything()
            return
        }
        _state.value = PurchaseState.Connecting
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _state.value = PurchaseState.Ready
                    queryEverything()
                } else {
                    _state.value = PurchaseState.Error
                }
            }

            override fun onBillingServiceDisconnected() {
                _state.value = PurchaseState.Idle
            }
        })
    }

    private fun queryEverything() {
        querySubs()
        queryInApp()
        refreshPurchases()
    }

    private fun querySubs() {
        val products = BillingIds.subProductIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        client.queryProductDetailsAsync(params) { _, queryResult ->
            queryResult.productDetailsList.forEach { subDetails[it.productId] = it }
            rebuildPlans()
        }
    }

    private fun queryInApp() {
        val products = BillingIds.inAppProductIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        client.queryProductDetailsAsync(params) { _, queryResult ->
            queryResult.productDetailsList.forEach { inAppDetails[it.productId] = it }
            rebuildPlans()
        }
    }

    private fun rebuildPlans() {
        val options = mutableListOf<PlanOption>()

        subDetails[BillingIds.SUB_PRO]?.subscriptionOfferDetails?.forEach { offer ->
            val phase = offer.pricingPhases.pricingPhaseList.lastOrNull() ?: return@forEach
            val isYearly = offer.basePlanId == BillingIds.PLAN_YEARLY
            options.add(
                PlanOption(
                    productId = BillingIds.SUB_PRO,
                    offerToken = offer.offerToken,
                    title = if (isYearly) "Yearly" else "Monthly",
                    price = phase.formattedPrice,
                    period = if (isYearly) "per year" else "per month",
                    badge = if (isYearly) "BEST VALUE" else null
                )
            )
        }

        inAppDetails[BillingIds.LIFETIME]?.let { d ->
            val price = d.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
            options.add(
                PlanOption(
                    productId = BillingIds.LIFETIME,
                    offerToken = null,
                    title = "Lifetime",
                    price = price,
                    period = "one-time",
                    badge = "PAY ONCE",
                    isLifetime = true
                )
            )
        }
        _plans.value = options
    }

    fun purchase(activity: Activity, option: PlanOption) {
        val details = if (option.isLifetime) inAppDetails[option.productId]
        else subDetails[option.productId]
        details ?: return

        val productParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
        if (!option.isLifetime && option.offerToken != null) {
            productParamsBuilder.setOfferToken(option.offerToken)
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParamsBuilder.build()))
            .build()

        _state.value = PurchaseState.Purchasing
        client.launchBillingFlow(activity, flowParams)
    }

    fun refreshPurchases() {
        val subParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS).build()
        client.queryPurchasesAsync(subParams) { _, purchases ->
            purchases.forEach { handlePurchase(it, silent = true) }
        }
        val inAppParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP).build()
        client.queryPurchasesAsync(inAppParams) { _, purchases ->
            purchases.forEach { handlePurchase(it, silent = true) }
        }
    }

    private fun handlePurchase(purchase: Purchase, silent: Boolean = false) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        setPro(true)
        if (!silent) _state.value = PurchaseState.Purchased

        // Canonical Firebase `purchase` event (deduped by token inside Analytics).
        purchase.products.forEach { pid ->
            val (micros, currency) = priceFor(pid)
            Analytics.purchase(purchase.purchaseToken, pid, micros, currency)
        }

        if (!purchase.isAcknowledged) {
            val ackParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken).build()
            client.acknowledgePurchase(ackParams) { }
        }
    }

    /** Best-effort price lookup from cached ProductDetails, for the purchase event. */
    private fun priceFor(productId: String): Pair<Long, String> {
        inAppDetails[productId]?.oneTimePurchaseOfferDetails?.let {
            return it.priceAmountMicros to it.priceCurrencyCode
        }
        subDetails[productId]?.subscriptionOfferDetails
            ?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.let {
                return it.priceAmountMicros to it.priceCurrencyCode
            }
        return 0L to ""
    }

    /** Writes the ONE premium flag (PremiumManager) → ads off immediately + saved. */
    private fun setPro(value: Boolean) {
        if (PremiumManager.isPremium != value) {
            PremiumManager.setPremium(value)
            onProChanged(value)
        }
    }

    /** Debug-only helper so the UI can be demoed without a live Play account. */
    fun grantProForDemo() = setPro(true)

    fun end() {
        if (client.isReady) client.endConnection()
    }
}