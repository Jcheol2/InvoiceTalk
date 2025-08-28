package com.hocheol.data.remoteDatasource.repository

import com.hocheol.data.remoteDatasource.RetrofitAPI
import com.hocheol.data.remoteDatasource.mapper.LoginMapper
import com.hocheol.data.remoteDatasource.toJsonRequestBody
import com.hocheol.domain.remote.model.LoginResponseDto
import com.hocheol.domain.remote.repository.RetrofitRepository
import javax.inject.Inject

class RetrofitRepositoryImpl @Inject constructor(
    private val retrofitAPI: RetrofitAPI
) : RetrofitRepository {
    override suspend fun login(requestBody: HashMap<String, Any>): LoginResponseDto {
        return try {
            val response = retrofitAPI.login(requestBody.toJsonRequestBody())
            LoginMapper.mapperToLoginResponseDto(response)
        } catch (e: Exception) {
            throw e
        }
    }
}