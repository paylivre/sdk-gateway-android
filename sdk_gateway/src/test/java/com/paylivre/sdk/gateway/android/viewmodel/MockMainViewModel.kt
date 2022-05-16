package com.paylivre.sdk.gateway.android.viewmodel

import com.paylivre.sdk.gateway.android.data.PaymentRepository
import com.paylivre.sdk.gateway.android.data.api.ApiService
import com.paylivre.sdk.gateway.android.data.api.RemoteDataSource
import com.paylivre.sdk.gateway.android.data.api.gson
import com.paylivre.sdk.gateway.android.ui.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MockMainViewModel (
    private val server: MockWebServer = MockWebServer(),
) {
    fun clientMock() =
        OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()

    val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
        .client(clientMock())
        .addConverterFactory(GsonConverterFactory.create(gson())).build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    var remoteDataSource = RemoteDataSource(apiService)
    var paymentRepository = PaymentRepository(remoteDataSource)
    var mainViewModel = MainViewModel(paymentRepository)
}