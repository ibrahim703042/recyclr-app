package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.ShopItemDto

/**
 * Service interface for Shop operations
 * Abstracts Firebase Firestore implementation
 */
interface ShopService {
    
    /**
     * Get all shop items from Firebase
     */
    suspend fun getAllShopItems(): Result<List<ShopItemDto>>
    
    /**
     * Get shop item by ID
     */
    suspend fun getShopItemById(id: String): Result<ShopItemDto?>
    
    /**
     * Add new shop item (admin operation)
     */
    suspend fun addShopItem(item: ShopItemDto): Result<Boolean>
    
    /**
     * Update shop item (admin operation)
     */
    suspend fun updateShopItem(item: ShopItemDto): Result<Boolean>
    
    /**
     * Delete shop item (admin operation)
     */
    suspend fun deleteShopItem(id: String): Result<Boolean>
}
