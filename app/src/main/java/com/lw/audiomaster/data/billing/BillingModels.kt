package com.lw.audiomaster.data.billing

object BillingIds {
    // Subscription with two base plans
    const val SUB_PRO = "audiomaster_pro"
    const val PLAN_MONTHLY = "monthly"
    const val PLAN_YEARLY = "yearly"

    // One-time
    const val LIFETIME = "audiomaster_lifetime"

    val subProductIds = listOf(SUB_PRO)
    val inAppProductIds = listOf(LIFETIME)
}

/** A displayable offer resolved from Play. */
data class PlanOption(
    val productId: String,
    val offerToken: String?,      // null for one-time products
    val title: String,
    val price: String,
    val period: String,           // e.g. "per month", "one-time"
    val badge: String? = null,
    val isLifetime: Boolean = false
)

enum class PurchaseState { Idle, Connecting, Ready, Purchasing, Purchased, Error }
