package com.paylivre.sdk.gateway.android

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.paylivre.sdk.gateway.android.data.PaymentRepository
import com.paylivre.sdk.gateway.android.data.api.*
import com.paylivre.sdk.gateway.android.data.model.deposit.CheckStatusDepositResponse
import com.paylivre.sdk.gateway.android.data.model.deposit.DataStatusDeposit
import com.paylivre.sdk.gateway.android.ui.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import org.junit.Assert


@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    @Test
    fun `test mainViewModel checkStatusDeposit success`() = runBlocking {
        val server = MockWebServer()
        server.start()
        val rawResponse =
            "{\"status\":\"success\",\"status_code\":200,\"message\":\"OK\",\"data\":{\"transaction_status_id\":null,\"deposit_status_id\":2}}"

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(rawResponse)
        )

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


        mainViewModel.checkStatusDeposit(1)


        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/transaction/deposit/status/1", request1.path)

        Assert.assertEquals(CheckStatusDepositResponse(
            "success",
            200,
            "OK",
            DataStatusDeposit(transaction_status_id = null, deposit_status_id = 2)
        ), mainViewModel.checkStatusDepositResponse.getOrAwaitValueTest())

        server.shutdown()
    }


    @Test
    fun `test mainViewModel checkStatusDeposit error api generic`() = runBlocking {
        val server = MockWebServer()
        server.start()
        val rawResponse =
            "{\"status\":\"success\",\"status_code\":200,\"message\":\"OK\",\"data\":{\"transaction_status_id\":null,\"deposit_status_id\":2}}"

        server.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(rawResponse)
        )

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


        mainViewModel.checkStatusDeposit(1)


        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/transaction/deposit/status/1", request1.path)

        Assert.assertEquals(CheckStatusDepositResponse(
            "error",
            0,
            "OK",
            null
        ), mainViewModel.checkStatusDepositResponse.getOrAwaitValueTest())

        server.shutdown()
    }


}