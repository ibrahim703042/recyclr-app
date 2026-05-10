package com.gdsc.recyclr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gdsc.recyclr.data.local.dao.CollectionPointDao
import com.gdsc.recyclr.data.local.dao.RedemptionDao
import com.gdsc.recyclr.data.local.dao.ScanRecordDao
import com.gdsc.recyclr.data.local.dao.ShopItemDao
import com.gdsc.recyclr.data.local.dao.UserImpactDao
import com.gdsc.recyclr.data.local.entities.CachedCollectionPointEntity
import com.gdsc.recyclr.data.local.entities.CachedRedemptionEntity
import com.gdsc.recyclr.data.local.entities.CachedScanRecordEntity
import com.gdsc.recyclr.data.local.entities.CachedShopItemEntity
import com.gdsc.recyclr.data.local.entities.CachedUserImpactEntity

@Database(
    entities = [
        CachedScanRecordEntity::class,
        CachedUserImpactEntity::class,
        CachedCollectionPointEntity::class,
        CachedShopItemEntity::class,
        CachedRedemptionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class RecyclrDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
    abstract fun userImpactDao(): UserImpactDao
    abstract fun collectionPointDao(): CollectionPointDao
    abstract fun shopItemDao(): ShopItemDao
    abstract fun redemptionDao(): RedemptionDao
}

