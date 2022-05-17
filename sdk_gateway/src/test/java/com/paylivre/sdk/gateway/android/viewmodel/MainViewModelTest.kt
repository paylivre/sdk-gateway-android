package com.paylivre.sdk.gateway.android.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.paylivre.sdk.gateway.android.FileTestsUtils
import com.paylivre.sdk.gateway.android.data.model.deposit.CheckStatusDepositResponse
import com.paylivre.sdk.gateway.android.data.model.deposit.DataStatusDeposit
import com.paylivre.sdk.gateway.android.data.model.order.*
import com.paylivre.sdk.gateway.android.data.model.transaction.CheckStatusTransactionResponse
import com.paylivre.sdk.gateway.android.domain.model.Currency
import com.paylivre.sdk.gateway.android.domain.model.DataStartCheckout
import com.paylivre.sdk.gateway.android.domain.model.Operation
import com.paylivre.sdk.gateway.android.getOrAwaitValueTest
import com.paylivre.sdk.gateway.android.utils.BASE_URL_ENVIRONMENT_DEV
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var fileTestsUtils = FileTestsUtils()

    @Test
    fun `test mainViewModel checkStatusDeposit success`() = runBlocking {
        //GIVEN
        val server = MockWebServer()
        server.start()

        val responseExpectedString =
            fileTestsUtils.loadJsonAsString("res_check_status_deposit_success.json")

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseExpectedString)
        )
        val mockMainViewModel = MockMainViewModel(server)

        //WHEN
        //Check Deposit Status
        mockMainViewModel.mainViewModel.checkStatusDeposit(1)

        //THEN
        //check Endpoint path is correct
        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/transaction/deposit/status/1", request1.path)

        //check the value returned to livedata
        Assert.assertEquals(CheckStatusDepositResponse(
            "success",
            200,
            "OK",
            DataStatusDeposit(transaction_status_id = null, deposit_status_id = 2)
        ), mockMainViewModel.mainViewModel.checkStatusDepositResponse.getOrAwaitValueTest())

        server.shutdown()
    }


    @Test
    fun `test mainViewModel checkStatusDeposit error api generic`() = runBlocking {
        //GIVEN
        val server = MockWebServer()
        server.start()

        val rawResponse = fileTestsUtils.loadJsonAsString("res_check_status_deposit_success.json")

        server.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(rawResponse)
        )
        val mockMainViewModel = MockMainViewModel(server)

        //WHEN
        //Check Deposit Status
        mockMainViewModel.mainViewModel.checkStatusDeposit(1)


        //THEN
        //check Endpoint path is correct
        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/transaction/deposit/status/1", request1.path)

        //check the value returned to livedata
        Assert.assertEquals(CheckStatusDepositResponse(
            "error",
            0,
            "OK",
            null
        ), mockMainViewModel.mainViewModel.checkStatusDepositResponse.getOrAwaitValueTest())

        server.shutdown()
    }

    @Test
    fun `test mainViewModel checkStatusOrder success`() = runBlocking {
        //GIVEN
        val server = MockWebServer()
        server.start()
        val rawResponse =
            "{\"operation\":5,\"operation_name\":\"Withdrawal\",\"full_name\":\"User Gateway Test\",\"document_number\":\"12345612312\",\"final_amount\":500,\"original_amount\":500,\"original_currency\":\"BRL\",\"currency\":\"BRL\",\"converted_amount\":500,\"taxes\":null,\"kyc_limits\":{\"available_amount\":9999999998999,\"limit\":9999999999999,\"used_limit\":1000,\"kyc_level\":\"3\",\"kyc_level_name\":\"Gold\"},\"order\":{\"id\":12345,\"type\":5,\"type_name\":\"Withdrawal\",\"status\":\"New\",\"status_id\":0,\"merchant_approval_status_id\":2,\"merchant_approval_status_name\":\"Completed\"},\"withdrawal_type_id\":4,\"withdrawal\":{\"id\":12345,\"status_id\":0,\"status_name\":\"New\"}}"

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(rawResponse)
        )
        val mockMainViewModel = MockMainViewModel(server)

        //WHEN
        //Check Status Order
        mockMainViewModel.mainViewModel.checkStatusOrder(
            CheckStatusOrderDataRequest(order_id = 1, token = "124a56s4da65f4654d56af4s65df")
        )

        //THEN
        //check Endpoint path is correct
        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/gateway/status/1/124a56s4da65f4654d56af4s65df", request1.path)

        val expectedDataResponse = Gson().fromJson(
            rawResponse, CheckStatusOrderDataResponse::class.java
        )

        //check the value returned to livedata
        Assert.assertEquals(CheckStatusOrderResponse(
            isLoading = false,
            isSuccess = true,
            data = expectedDataResponse
        ), mockMainViewModel.mainViewModel.checkStatusOrderDataResponse.getOrAwaitValueTest())

        server.shutdown()
    }


    @Test
    fun `test mainViewModel checkStatusOrder failure api error generic `() = runBlocking {
        //GIVEN
        val server = MockWebServer()
        server.start()
        val rawResponse =
            "{\"message\":\"error\"}}"

        server.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(rawResponse)
        )

        val mockMainViewModel = MockMainViewModel(server)

        //WHEN
        //Check Status Order
        mockMainViewModel.mainViewModel.checkStatusOrder(
            CheckStatusOrderDataRequest(order_id = 1, token = "124a56s4da65f4654d56af4s65df")
        )

        //THEN
        //check Endpoint path is correct
        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/gateway/status/1/124a56s4da65f4654d56af4s65df", request1.path)

        //check the value returned to livedata
        Assert.assertEquals(CheckStatusOrderResponse(
            isLoading = false,
            isSuccess = false,
            data = null,
            error = ErrorTransaction(message = "title_unexpected_error",
                messageDetails = "title_unexpected_error_body",
                error = null,
                errors = null,
                errorTags = "UX000",
                original_message = null)
        ), mockMainViewModel.mainViewModel.checkStatusOrderDataResponse.getOrAwaitValueTest())

        server.shutdown()
    }

    @Test
    fun `test mainViewModel checkStatusTransaction success`() = runBlocking {
        //GIVEN
        val server = MockWebServer()
        server.start()
        val rawResponse =
            "{\"status\":\"success\",\"status_code\":200,\"message\":\"OK\",\"data\":{\"transaction_status_id\":null,\"deposit_status_id\":2}}"

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(rawResponse)
        )
        val mockMainViewModel = MockMainViewModel(server)

        //WHEN
        //Check Status Transaction
        mockMainViewModel.mainViewModel.checkStatusTransaction(transactionId = 1)

        //THEN
        //check Endpoint path is correct
        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/transaction/status/1", request1.path)

        val expectedDataResponse = Gson().fromJson(
            rawResponse, CheckStatusTransactionResponse::class.java
        )

        //check the value returned to livedata
        Assert.assertEquals(expectedDataResponse,
            mockMainViewModel.mainViewModel.checkStatusTransactionResponse.getOrAwaitValueTest())

        server.shutdown()
    }

    @Test
    fun `test mainViewModel checkStatusTransaction failure error generic`() = runBlocking {
        //GIVEN
        val server = MockWebServer()
        server.start()
        val rawResponse = "{\"message\":\"error\"}"

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(rawResponse)
        )
        val mockMainViewModel = MockMainViewModel(server)

        //WHEN
        //Check Status Transaction
        mockMainViewModel.mainViewModel.checkStatusTransaction(transactionId = 1)

        //THEN
        //check Endpoint path is correct
        val request1 = server.takeRequest()
        Assert.assertEquals("/api/v2/transaction/status/1", request1.path)


        //check the value returned to livedata
        Assert.assertEquals(
            CheckStatusTransactionResponse(
                status = "error",
                status_code = 0,
                message = "title_unexpected_error",
                data = null
            ), mockMainViewModel.mainViewModel.checkStatusTransactionResponse.getOrAwaitValueTest())

        server.shutdown()
    }


    @Test
    fun `test mainViewModel setStatusResponseCheckServices`() = runBlocking {
        val mockMainViewModel = MockMainViewModel()
        mockMainViewModel.mainViewModel.setStatusResponseCheckServices(null)
        Assert.assertEquals(null,
            mockMainViewModel.mainViewModel.statusResponseCheckServices.getOrAwaitValueTest())
    }

    @Test
    fun `test mainViewModel setStatusWithdrawOrder`() = runBlocking {
        val mockMainViewModel = MockMainViewModel()
        mockMainViewModel.mainViewModel.setStatusWithdrawOrder(
            StatusWithdrawOrder(null, null, null, null)
        )
        Assert.assertEquals(
            StatusWithdrawOrder(null, null, null, null),
            mockMainViewModel.mainViewModel.statusWithdrawOrder.getOrAwaitValueTest())
    }


    @Test
    fun `test mainViewModel setDataStartCheckout`() = runBlocking {
        val mockMainViewModel = MockMainViewModel()
        val mockDataStartCheckout = DataStartCheckout(
            10,
            "123asd4a56sf4a56s4d65as4d",
            Operation.DEPOSIT.code,
            "12asd323",
            500,
            Currency.BRL.currency,
            1,
            "1d2a3sd",
            "https://google.com",
            "user_gateway_test@tutanota.com",
            "61317581075",
            BASE_URL_ENVIRONMENT_DEV,
            1,
            signature = "123asd4a56s4f65a4sd1as32d1gfasfgafdgsdfg"
        )
        mockMainViewModel.mainViewModel.setDataStartCheckout(
            mockDataStartCheckout
        )
        Assert.assertEquals(
            mockDataStartCheckout,
            mockMainViewModel.mainViewModel.dataStartCheckout.getOrAwaitValueTest())
    }

    @Test
    fun `test mainViewModel setOriginTypeInsertProof`() = runBlocking {
        val mockMainViewModel = MockMainViewModel()
        mockMainViewModel.mainViewModel.setOriginTypeInsertProof(null)
        Assert.assertEquals(
            null,
            mockMainViewModel.mainViewModel.origin_type_insert_proof.getOrAwaitValueTest())
    }

    @Test
    fun `test mainViewModel setProofImageUri`() = runBlocking {
        val mockMainViewModel = MockMainViewModel()
        val mockUri: Uri = mockk()
        mockMainViewModel.mainViewModel.setProofImageUri(mockUri)
        Assert.assertEquals(
            mockUri,
            mockMainViewModel.mainViewModel.proof_image_uri.getOrAwaitValueTest())
    }

    @Test
    fun `test mainViewModel setSelectedBankAccountWireTransfer`() = runBlocking {
        val mockMainViewModel = MockMainViewModel()
        mockMainViewModel.mainViewModel.setSelectedBankAccountWireTransfer(null)
        Assert.assertEquals(
            null,
            mockMainViewModel.mainViewModel.selectedBankAccountWireTransfer.getOrAwaitValueTest())
    }


}