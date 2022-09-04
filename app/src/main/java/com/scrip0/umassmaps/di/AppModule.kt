package com.scrip0.umassmaps.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.scrip0.umassmaps.db.local.LocalBuildingDatabase
import com.scrip0.umassmaps.other.Constants.LOCAL_BUILDING_DATABASE_NAME
import com.scrip0.umassmaps.repositories.DirectionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Singleton
	@Provides
	fun provideLocalBuildingDatabase(
		@ApplicationContext app: Context
	) = Room.databaseBuilder(
		app,
		LocalBuildingDatabase::class.java,
		LOCAL_BUILDING_DATABASE_NAME
	).fallbackToDestructiveMigration().build()

	@Singleton
	@Provides
	fun provideLocalBuildingDao(db: LocalBuildingDatabase) = db.getBuildingDao()

	@Singleton
	@Provides
	fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

	@Singleton
	@Provides
	fun providesDirectionsRepository() = DirectionsRepository()
}