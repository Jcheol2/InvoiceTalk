package com.hocheol.domain.remote.repository

import com.hocheol.domain.remote.model.LoginResponseDto

interface RetrofitRepository {
    suspend fun login(requestBody: HashMap<String, Any>): LoginResponseDto
}