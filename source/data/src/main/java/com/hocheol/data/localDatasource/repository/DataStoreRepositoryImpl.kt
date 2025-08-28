package com.hocheol.data.localDatasource.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hocheol.domain.local.repository.DataStoreRepository
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {
    private val Context.dataStore by preferencesDataStore(
        name = "invoice_talk_pref",
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        )
    )

    object PreferenceKeys {
        val USER_ID = stringPreferencesKey("userId")
    }

    private suspend fun <T> DataStore<Preferences>.storeValue(key: Preferences.Key<T>, value: T) {
        try {
            edit { preferences ->
                preferences[key] = value
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, "Error saving value", e)
        }
    }

    private suspend inline fun <T : Any> DataStore<Preferences>.readValue(key: Preferences.Key<T>, defaultValue: T): T {
        return data.catch { recoverOrThrow(it) }.map { it[key] }.firstOrNull() ?: defaultValue
    }

    private suspend fun FlowCollector<Preferences>.recoverOrThrow(throwable: Throwable) {
        if (throwable is IOException) emit(emptyPreferences()) else throw throwable
    }

    override suspend fun clearAllPreferences() {
        context.dataStore.edit { settings ->
            settings.clear()
        }
    }

    override suspend fun saveUserId(userId: String) = context.dataStore.storeValue(PreferenceKeys.USER_ID,  userId)
    override suspend fun getUserId(): String = context.dataStore.readValue(PreferenceKeys.USER_ID, "")
}