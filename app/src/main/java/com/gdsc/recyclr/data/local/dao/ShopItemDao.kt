package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.CachedShopItemEntity

@Dao
interface ShopItemDao {
    @Query("SELECT * FROM shop_items")
    suspend fun all(): List<CachedShopItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedShopItemEntity>)
}

