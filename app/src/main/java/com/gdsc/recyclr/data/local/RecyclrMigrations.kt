package com.gdsc.recyclr.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 : tables `scan_records` et `user_impact` (cache invité minimal).
 * v2 : ajout des caches boutique, points de collecte et redemptions.
 * v3 : cache lookups code-barres et file enfilement demandes de collecte hors ligne.
 */
object RecyclrMigrations {

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
