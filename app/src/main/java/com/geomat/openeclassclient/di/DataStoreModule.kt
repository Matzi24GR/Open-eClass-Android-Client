package com.geomat.openeclassclient.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CredentialsModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile("login")
            }
        )
        return dataStore
    }

    @Provides
    @Singleton
    fun provideCredentials(repo: CredentialsRepository): Flow<Credentials> {
        return repo.credentialsFlow
    }

}