package com.hocheol.data.di

import android.content.Context
import com.hocheol.data.localDatasource.repository.DataStoreRepositoryImpl
import com.hocheol.data.localDatasource.repository.LogRepositoryImpl
import com.hocheol.domain.local.repository.DataStoreRepository
import com.hocheol.domain.local.repository.LogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository = DataStoreRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideLogRepository(
        @ApplicationContext context: Context
    ): LogRepository = LogRepositoryImpl(context)
}