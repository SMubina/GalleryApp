package com.example.galleryapp.di

import android.content.ContentResolver
import android.content.Context
import com.example.galleryapp.data.repository.MediaRepository
import com.example.galleryapp.data.repository.MediaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * application module to provide required dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * provides content resolver dependency
     */
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    /**
     * provide media repository dependency
     */
    @Provides
    @Singleton
    fun provideMediaRepository(
        @ApplicationContext context: Context
    ): MediaRepository {
        return MediaRepositoryImpl(context)
    }


}