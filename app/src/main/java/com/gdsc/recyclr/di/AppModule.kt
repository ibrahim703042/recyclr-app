package com.gdsc.recyclr.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.gdsc.recyclr.data.repository.AuthRepositoryImpl
import com.gdsc.recyclr.data.repository.EngagementRepositoryImpl
import com.gdsc.recyclr.data.repository.CollectionPointsRepositoryImpl
import com.gdsc.recyclr.data.repository.ImpactRepositoryImpl
import com.gdsc.recyclr.data.repository.RedemptionRepositoryImpl
import com.gdsc.recyclr.data.repository.SettingsRepositoryImpl
import com.gdsc.recyclr.data.repository.ScanRecordsRepositoryImpl
import com.gdsc.recyclr.data.repository.ShopRepositoryImpl
import com.gdsc.recyclr.data.ml.TensorflowLiteWasteDetector
import com.gdsc.recyclr.data.local.RecyclrMigrations
import com.gdsc.recyclr.data.location.GeofenceManager
import com.gdsc.recyclr.data.location.GeofenceManagerImpl
import com.gdsc.recyclr.data.local.RecyclrDatabase
import com.gdsc.recyclr.data.local.dao.BarcodeCacheDao
import com.gdsc.recyclr.data.local.dao.CollectionPointDao
import com.gdsc.recyclr.data.local.dao.ChatMessageDao
import com.gdsc.recyclr.data.local.dao.NotificationDao
import com.gdsc.recyclr.data.local.dao.PickupQueueDao
import com.gdsc.recyclr.data.local.dao.WishlistDao
import com.gdsc.recyclr.data.local.dao.RedemptionDao
import com.gdsc.recyclr.data.local.dao.ScanRecordDao
import com.gdsc.recyclr.data.local.dao.ShopItemDao
import com.gdsc.recyclr.data.local.dao.UserImpactDao
import com.gdsc.recyclr.data.service.AuthService
import com.gdsc.recyclr.data.service.CollectionPointsService
import com.gdsc.recyclr.data.service.ImpactService
import com.gdsc.recyclr.data.service.RedemptionService
import com.gdsc.recyclr.data.service.ScanRecordsService
import com.gdsc.recyclr.data.service.ShopService
import com.gdsc.recyclr.data.service.impl.AuthServiceImpl
import com.gdsc.recyclr.data.service.impl.CollectionPointsServiceImpl
import com.gdsc.recyclr.data.service.impl.ImpactServiceImpl
import com.gdsc.recyclr.data.service.impl.RedemptionServiceImpl
import com.gdsc.recyclr.data.service.impl.ScanRecordsServiceImpl
import com.gdsc.recyclr.data.service.impl.ShopServiceImpl
import com.gdsc.recyclr.data.service.EngagementService
import com.gdsc.recyclr.data.service.impl.EngagementServiceImpl
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.CollectionPointsRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import com.gdsc.recyclr.domain.repository.SettingsRepository
import com.gdsc.recyclr.domain.repository.ScanRecordsRepository
import com.gdsc.recyclr.domain.repository.ShopRepository
import com.gdsc.recyclr.domain.repository.RedemptionRepository
import com.gdsc.recyclr.domain.ml.WasteDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import android.content.Context
import androidx.room.Room

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Firebase Providers
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    // Service Providers
    @Provides
    @Singleton
    fun provideAuthService(
        firebaseAuth: FirebaseAuth, 
        storage: FirebaseStorage,
        firestore: FirebaseFirestore
    ): AuthService {
        return AuthServiceImpl(firebaseAuth, storage, firestore)
    }
    
    @Provides
    @Singleton
    fun provideShopService(firestore: FirebaseFirestore): ShopService {
        return ShopServiceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCollectionPointsService(firestore: FirebaseFirestore): CollectionPointsService {
        return CollectionPointsServiceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideScanRecordsService(firestore: FirebaseFirestore, storage: FirebaseStorage): ScanRecordsService {
        return ScanRecordsServiceImpl(firestore, storage)
    }

    @Provides
    @Singleton
    fun provideImpactService(firestore: FirebaseFirestore): ImpactService {
        return ImpactServiceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideRedemptionService(firestore: FirebaseFirestore): RedemptionService {
        return RedemptionServiceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideEngagementService(firestore: FirebaseFirestore): EngagementService {
        return EngagementServiceImpl(firestore)
    }
    
    // Repository Providers
    @Provides
    @Singleton
    fun provideAuthRepository(authService: AuthService): AuthRepository {
        return AuthRepositoryImpl(authService)
    }
    
    @Provides
    @Singleton
    fun provideShopRepository(shopService: ShopService, dao: ShopItemDao): ShopRepository {
        return ShopRepositoryImpl(shopService, dao)
    }

    @Provides
    @Singleton
    fun provideCollectionPointsRepository(service: CollectionPointsService, dao: CollectionPointDao): CollectionPointsRepository {
        return CollectionPointsRepositoryImpl(service, dao)
    }

    @Provides
    @Singleton
    fun provideScanRecordsRepository(service: ScanRecordsService, dao: ScanRecordDao): ScanRecordsRepository {
        return ScanRecordsRepositoryImpl(service, dao)
    }

    @Provides
    @Singleton
    fun provideImpactRepository(service: ImpactService, dao: UserImpactDao, authRepository: AuthRepository): ImpactRepository {
        return ImpactRepositoryImpl(service, dao, authRepository)
    }

    @Provides
    @Singleton
    fun provideRedemptionRepository(
        service: RedemptionService,
        dao: RedemptionDao,
        impactRepository: ImpactRepository,
    ): RedemptionRepository {
        return RedemptionRepositoryImpl(service, dao, impactRepository)
    }

    @Provides
    @Singleton
    fun provideWasteDetector(impl: TensorflowLiteWasteDetector): WasteDetector = impl

    @Provides
    @Singleton
    fun provideEngagementRepository(impl: EngagementRepositoryImpl): EngagementRepository = impl

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideGeofenceManager(
        @ApplicationContext context: Context
    ): GeofenceManager = GeofenceManagerImpl(context)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RecyclrDatabase {
        return Room.databaseBuilder(context, RecyclrDatabase::class.java, "recyclr.db")
            .addMigrations(
                RecyclrMigrations.MIGRATION_1_2,
                RecyclrMigrations.MIGRATION_2_3,
                RecyclrMigrations.MIGRATION_3_4,
                RecyclrMigrations.MIGRATION_4_5,
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideScanRecordDao(db: RecyclrDatabase): ScanRecordDao = db.scanRecordDao()

    @Provides
    fun provideUserImpactDao(db: RecyclrDatabase): UserImpactDao = db.userImpactDao()

    @Provides
    fun provideCollectionPointDao(db: RecyclrDatabase): CollectionPointDao = db.collectionPointDao()

    @Provides
    fun provideShopItemDao(db: RecyclrDatabase): ShopItemDao = db.shopItemDao()

    @Provides
    fun provideRedemptionDao(db: RecyclrDatabase): RedemptionDao = db.redemptionDao()

    @Provides
    fun provideBarcodeCacheDao(db: RecyclrDatabase): BarcodeCacheDao = db.barcodeCacheDao()

    @Provides
    fun providePickupQueueDao(db: RecyclrDatabase): PickupQueueDao = db.pickupQueueDao()

    @Provides
    fun provideNotificationDao(db: RecyclrDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideChatMessageDao(db: RecyclrDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides
    fun provideWishlistDao(db: RecyclrDatabase): WishlistDao = db.wishlistDao()
}
