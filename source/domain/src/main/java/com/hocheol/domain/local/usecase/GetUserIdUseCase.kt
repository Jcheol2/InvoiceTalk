package com.hocheol.domain.local.usecase

import com.hocheol.domain.local.repository.DataStoreRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            val userId = dataStoreRepository.getUserId()
            if (userId.isNotEmpty()) {
                return Result.success(userId)
            }

            Result.failure(Exception("User Id is Empty"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}