package com.gdsc.recyclr.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 : tables `scan_records` et `user_impact` (cache invité minimal).
 * v2 : ajout des caches boutique, points de collecte et redemptions.
 * v3 : cache lookups code-barres et file enfilement demandes de collecte hors ligne.
 * v4 : payload JSON produits, notifications, chat support, wishlist.
 * v5 : displayName et role dans user_impact (classements).
 */
object RecyclrMigrations {

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE user_impact ADD COLUMN displayName TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE user_impact ADD COLUMN role TEXT NOT NULL DEFAULT 'USER'")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE shop_items ADD COLUMN payloadJson TEXT NOT NULL DEFAULT '{}'")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `app_notifications` (
                    `id` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `body` TEXT NOT NULL,
                    `createdAtMillis` INTEGER NOT NULL,
                    `isRead` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `chat_messages` (
                    `id` TEXT NOT NULL,
                    `threadId` TEXT NOT NULL,
                    `body` TEXT NOT NULL,
                    `fromUser` INTEGER NOT NULL,
                    `sentAtMillis` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `wishlist_items` (
                    `userId` TEXT NOT NULL,
                    `productId` TEXT NOT NULL,
                    `addedAtMillis` INTEGER NOT NULL,
                    PRIMARY KEY(`userId`, `productId`)
                )
                """.trimIndent(),
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `barcode_cache` (
                    `barcode` TEXT NOT NULL,
                    `itemLabel` TEXT NOT NULL,
                    `points` INTEGER NOT NULL,
                    `fetchedAtMillis` INTEGER NOT NULL,
                    PRIMARY KEY(`barcode`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `pickup_requests_local` (
                    `id` TEXT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `address` TEXT NOT NULL,
                    `itemsCsv` TEXT NOT NULL,
                    `estimatedKg` REAL NOT NULL,
                    `repeatEveryWeeks` INTEGER,
                    `createdAtMillis` INTEGER NOT NULL,
                    `synced` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `collection_points` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `lat` REAL NOT NULL,
                    `lng` REAL NOT NULL,
                    `typesCsv` TEXT NOT NULL,
                    `hours` TEXT NOT NULL,
                    `phone` TEXT,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `shop_items` (
                    `id` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `price` INTEGER NOT NULL,
                    `description` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `imageUrl` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `redemptions` (
                    `id` TEXT NOT NULL,
                    `userId` TEXT NOT NULL,
                    `shopItemId` TEXT NOT NULL,
                    `shopItemTitle` TEXT NOT NULL,
                    `pointsCost` INTEGER NOT NULL,
                    `timestampMillis` INTEGER NOT NULL,
                    `qrPayload` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }
}
