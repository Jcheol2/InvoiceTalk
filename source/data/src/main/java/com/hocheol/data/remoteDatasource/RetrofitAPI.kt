package com.hocheol.data.remoteDatasource

import com.hocheol.data.remoteDatasource.model.LoginResponse
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("api/v1/users")
    suspend fun signUp(@Body requestBody: RequestBody) : Response

    @POST("api/v1/users/login")
    suspend fun login(@Body requestBody: RequestBody) : LoginResponse

    @POST("api/v1/invoices/scan")
    suspend fun scanInvoice(@Body requestBody: RequestBody) : Response

    @GET("api/v1/invoices")
    suspend fun getInvoices(@Body requestBody: RequestBody) : Response

    @GET("api/v1/invoices/{id}")
    suspend fun getInvoiceDetail(@Body requestBody: RequestBody) : Response

    @GET("api/v1/invoices/count")
    suspend fun getInvoiceCount(@Body requestBody: RequestBody) : Response
}