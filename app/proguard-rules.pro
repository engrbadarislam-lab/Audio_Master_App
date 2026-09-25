# Play Billing
-keep class com.android.billingclient.** { *; }
# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**
# Media3
-dontwarn androidx.media3.**
# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }

# ---- AdMob (Google Mobile Ads) ----
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# ---- Meta Audience Network mediation adapter ----
-keep class com.google.ads.mediation.facebook.** { *; }
-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.ads.**
-keep public class com.google.android.gms.ads.mediation.** { public *; }

# ---- User Messaging Platform (UMP consent) ----
-keep class com.google.android.ump.** { *; }
-dontwarn com.google.android.ump.**
