package com.kanzan.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kanzan.core.data.repository.AutoClickRepositoryImpl
import com.kanzan.core.data.repository.MacroRepositoryImpl
import com.kanzan.core.data.repository.SettingsRepositoryImpl
import com.kanzan.core.data.util.dataStore
import com.kanzan.core.domain.repository.AutoClickRepository
import com.kanzan.core.domain.repository.MacroRepository
import com.kanzan.core.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Data layer repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideMacroRepository(
        @ApplicationContext context: Context
    ): MacroRepository {
        return MacroRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideAutoClickRepository(
        @ApplicationContext context: Context
    ): AutoClickRepository {
        return AutoClickRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ): SettingsRepository {
        return SettingsRepositoryImpl(context, dataStore)
    }
}
