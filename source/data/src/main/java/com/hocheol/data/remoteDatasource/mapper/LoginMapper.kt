package com.hocheol.data.remoteDatasource.mapper

import com.hocheol.data.remoteDatasource.model.LoginResponse
import com.hocheol.domain.remote.model.LoginResponseDto

object LoginMapper {
    fun mapperToLoginResponseDto(loginResponse: LoginResponse): LoginResponseDto {
        return LoginResponseDto(
            userId = loginResponse.userId
        )
    }
}