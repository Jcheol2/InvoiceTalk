package com.hocheol.data.di

import com.hocheol.data.Constants
import com.hocheol.data.remoteDatasource.RetrofitAPI
import com.hocheol.data.remoteDatasource.repository.RetrofitRepositoryImpl
import com.hocheol.domain.local.repository.DataStoreRepository
import com.hocheol.domain.remote.repository.RetrofitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        dataStoreRepository: DataStoreRepository
    ): OkHttpClient {
        val addTokenForHeaderInterceptor = createAddUserIdForHeaderInterceptor(dataStoreRepository) // 헤더 사용자 ID 삽입 Interceptor
        val (sslSocketFactory, trustManager) = createTrustAllSslSocketFactory() // SSL 인증 우회 소켓 생성
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .sslSocketFactory(sslSocketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(addTokenForHeaderInterceptor)
            .build()
    }

    private fun createAddUserIdForHeaderInterceptor(
        dataStoreRepository: DataStoreRepository
    ): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")

            runBlocking {
                val userId = dataStoreRepository.getUserId()
                if (userId.isNotEmpty()) requestBuilder.addHeader("user-id", userId)
            }

            chain.proceed(requestBuilder.build())
        }
    }

    private fun createTrustAllSslSocketFactory(): Pair<SSLSocketFactory, X509TrustManager> {
        val trustAllCerts = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Trust all client certificates
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Trust all server certificates
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(trustAllCerts), SecureRandom())
        return Pair(sslContext.socketFactory, trustAllCerts)
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): RetrofitAPI {
        return retrofit.create(RetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitRepository(
        retrofitAPI: RetrofitAPI
    ): RetrofitRepository {
        return RetrofitRepositoryImpl(retrofitAPI)
    }
}