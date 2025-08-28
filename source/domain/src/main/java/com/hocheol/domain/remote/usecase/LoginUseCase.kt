package com.hocheol.domain.remote.usecase

import com.hocheol.domain.local.repository.DataStoreRepository
import com.hocheol.domain.remote.repository.RetrofitRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val retrofitRepository: RetrofitRepository,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(inputUserId: String, inputUserPw: String): Result<Boolean> {
        return runCatching {
            val requestBody : HashMap<String, Any> = hashMapOf(
                "userName" to inputUserId,
                "password" to inputUserPw
            )

            val loginResponse = retrofitRepository.login(requestBody)
            dataStoreRepository.saveUserId(loginResponse.userId)
            true
        }
    }
}
