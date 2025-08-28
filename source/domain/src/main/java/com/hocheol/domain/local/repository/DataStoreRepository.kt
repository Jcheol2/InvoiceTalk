package com.hocheol.domain.local.repository

interface DataStoreRepository {
    suspend fun clearAllPreferences()

    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String
}