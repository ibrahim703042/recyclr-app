package com.gdsc.recyclr.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 : tables `scan_records` et `user_impact` (cache invité minimal).
 * v2 : ajout des caches boutique, points de collecte et redemptions.
 */
object RecyclrMigrations {

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
