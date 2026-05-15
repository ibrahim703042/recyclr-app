package com.gdsc.recyclr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gdsc.recyclr.data.local.dao.BarcodeCacheDao
import com.gdsc.recyclr.data.local.dao.ChatMessageDao
import com.gdsc.recyclr.data.local.dao.CollectionPointDao
import com.gdsc.recyclr.data.local.dao.NotificationDao
import com.gdsc.recyclr.data.local.dao.PickupQueueDao
import com.gdsc.recyclr.data.local.dao.RedemptionDao
import com.gdsc.recyclr.data.local.dao.ScanRecordDao
import com.gdsc.recyclr.data.local.dao.ShopItemDao
import com.gdsc.recyclr.data.local.dao.UserImpactDao
import com.gdsc.recyclr.data.local.dao.WishlistDao
import com.gdsc.recyclr.data.local.entities.CachedBarcodeEntity
import com.gdsc.recyclr.data.local.entities.CachedChatMessageEntity
import com.gdsc.recyclr.data.local.entities.CachedCollectionPointEntity
import com.gdsc.recyclr.data.local.entities.CachedNotificationEntity
import com.gdsc.recyclr.data.local.entities.CachedRedemptionEntity
import com.gdsc.recyclr.data.local.entities.CachedScanRecordEntity
import com.gdsc.recyclr.data.local.entities.CachedShopItemEntity
import com.gdsc.recyclr.data.local.entities.CachedUserImpactEntity
import com.gdsc.recyclr.data.local.entities.CachedWishlistEntity
import com.gdsc.recyclr.data.local.entities.LocalPickupRequestEntity

@Database(
    entities = [
        CachedScanRecordEntity::class,
        CachedUserImpactEntity::class,
        CachedCollectionPointEntity::class,
        CachedShopItemEntity::class,
        CachedRedemptionEntity::class,
        CachedBarcodeEntity::class,
        LocalPickupRequestEntity::class,
        CachedNotificationEntity::class,
        CachedChatMessageEntity::class,
        CachedWishlistEntity::class,
    ],
    version = 5,
    exportSchema = true
)
abstract class RecyclrDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
    abstract fun userImpactDao(): UserImpactDao
    abstract fun collectionPointDao(): CollectionPointDao
    abstract fun shopItemDao(): ShopItemDao
    abstract fun redemptionDao(): RedemptionDao
    abstract fun barcodeCacheDao(): BarcodeCacheDao
    abstract fun pickupQueueDao(): PickupQueueDao
    abstract fun notificationDao(): NotificationDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun wishlistDao(): WishlistDao
}

