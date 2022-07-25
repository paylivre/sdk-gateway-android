package com.paylivre.sdk.gateway.android.data.model.order

import com.paylivre.sdk.gateway.android.FileTestsUtils
import com.paylivre.sdk.gateway.android.data.MockCallResponseBody
import com.paylivre.sdk.gateway.android.data.RemoteDataSourceTestUtils
import com.paylivre.sdk.gateway.android.data.getGenericErrorData
import com.paylivre.sdk.gateway.android.data.model.order.KYC.LimitsKyc
import com.paylivre.sdk.gateway.android.services.log.LogErrorScope
import com.paylivre.sdk.gateway.android.services.log.LogErrorScopeImpl
import com.paylivre.sdk.gateway.android.services.log.LogErrorServiceImplTest
import com.paylivre.sdk.gateway.android.services.log.LogEventsServiceImplTest
import org.junit.Assert
import org.junit.Test

class HandleResponseCheckOrderTest {

    @Test
    fun `CASE 01, test getResponseJsonCheckStatusOrder given body() is null`() {
        val mockCallResponseBody = MockCallResponseBody(code = 400)
        var isErrorCatch = false

        //WHEN
        try {
            getResponseJsonCheckStatusOrder(mockCallResponseBody.execute())
        } catch (e: Exception) {
            isErrorCatch = true
        }

        Assert.assertTrue(isErrorCatch)
    }

    @Test
    fun `CASE 1, handleResponseCheckStatusOrder, given request isSuccessful and order in response is null`() {
        //GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val logEventsService = LogEventsServiceImplTest()
        val checkStatusOrderResponseDataString = MockCheckOrderResponse.checkOrderResponseOrderNull
        val mockCallResponseBody = MockCallResponseBody(code = 200,
            rawResponseBodySuccess = checkStatusOrderResponseDataString)
        val orderDataRequest = CheckStatusOrderDataRequest(
            order_id = 10,
            token = "123456dasdadfghjasldkfgjhalsdkjfhalksdf",
        )

        var dataResponseReceived: CheckStatusOrderDataResponse? = null
        var dataErrorReceived: ErrorTransaction? = null

        fun onResponse(data: CheckStatusOrderDataResponse?, error: ErrorTransaction?) {
            dataResponseReceived = data
            dataErrorReceived = error
        }

        //WHEN
        handleResponseCheckStatusOrder(orderDataRequest,
            response = mockCallResponseBody.execute(),
            ::onResponse,
            logEventsService = logEventsService,
            logErrorService = logErrorService
        )

        val dataResponseExpected = CheckStatusOrderDataResponse(
            error = null,
            operation = 5,
            operation_name = "Withdrawal",
            full_name = "User Gateway Test",
            document_number = "61317581075",
            final_amount = 500,
            original_amount = 500,
            original_currency = "BRL",
            currency = "BRL",
            converted_amount = 500,
            kyc_limits = LimitsKyc(
                available_amount = 9.999999926808E12,
                limit = 9.999999999999E12,
                kyc_level = "3",
                kyc_level_name = "Gold",
                used_limit = 73191.0),
            order = null,
            withdrawal_type_id = 4,
            withdrawal = Withdraw(id = 16741, status_id = 0, status_name = "New")
        )

        //THEN
        Assert.assertEquals(null, dataErrorReceived)
        Assert.assertEquals(dataResponseExpected, dataResponseReceived)

        val logEventAnalyticsWithParamsExpected =
            logEventsService.getLogEventAnalyticsWithParamsList(
                "SuccessCheckStatusOrder",
                Pair("order_id", "null"),
                Pair("order_type", "null"),
                Pair("order_type_name", "null"),
                Pair("order_status_id", "null"),
                Pair("order_status", "null"),
                Pair("merchant_approval_status_id", "null"),
                Pair("merchant_approval_status_name", "null")
            )

        //test logs
        Assert.assertEquals(logEventAnalyticsWithParamsExpected,
            logEventsService.logEventAnalyticsWithParams)
    }

    @Test
    fun `CASE 2, handleResponseCheckStatusOrder, given request is failure`() {
        //GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val logEventsService = LogEventsServiceImplTest()
        val mockCallResponseBody = MockCallResponseBody(code = 400)
        val orderDataRequest = CheckStatusOrderDataRequest(
            order_id = 10,
            token = "123456dasdadfghjasldkfgjhalsdkjfhalksdf",
        )

        var dataResponseReceived: CheckStatusOrderDataResponse? = null
        var dataErrorReceived: ErrorTransaction? = null

        fun onResponse(data: CheckStatusOrderDataResponse?, error: ErrorTransaction?) {
            dataResponseReceived = data
            dataErrorReceived = error
        }

        //WHEN
        handleResponseCheckStatusOrder(orderDataRequest,
            response = mockCallResponseBody.execute(),
            ::onResponse,
            logEventsService = logEventsService,
            logErrorService = logErrorService
        )

        val responseErrorExpected = ErrorTransaction(
            message = "invalid_data_error",
            messageDetails = "",
            error = null,
            errors = null,
            errorTags = null,
            original_message = "title_unexpected_error")

        //THEN
        Assert.assertEquals(responseErrorExpected, dataErrorReceived)
        Assert.assertEquals(null, dataResponseReceived)

        val logEventAnalyticsWithParamsExpected =
            logEventsService.getLogEventAnalyticsWithParamsList(
                "ErrorTransaction",
                Pair("order_status", "null"),
                Pair("merchant_approval_status_name", "null"),
                Pair("order_status_id", "null"),
                Pair("order_type_name", "null"),
                Pair("order_id", "null"),
                Pair("order_type", "null"),
                Pair("merchant_approval_status_id", "null")
            )

        //test logs event analytics
        Assert.assertEquals(logEventAnalyticsWithParamsExpected,
            logEventsService.logEventAnalyticsWithParams)

        //test sentry logs
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair("request_body_json_gateway", orderDataRequest.toString()),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API | status_code: 400 (api/v2/gateway/status)"
        )

        Assert.assertEquals(listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras)
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)

        Assert.assertEquals(listOf(Pair("status_code_gateway", "400")),
            logErrorService.logErrorScopeReceived?.getTags())
        Assert.assertEquals(listOf<Pair<String, Any>>(Pair("request_body_gateway",
            orderDataRequest)), logErrorService.logErrorScopeReceived?.getContexts())

    }

    @Test
    fun `CASE 3, handleResponseCheckStatusOrder, given catch error`() {
        //exemplo: quando for um isSuccessful e receber um data inesperado
        //GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val logEventsService = LogEventsServiceImplTest()
        val mockCallResponseBody =
            MockCallResponseBody(code = 200, rawResponseBodySuccess = "invalid_data")
        val orderDataRequest = CheckStatusOrderDataRequest(
            order_id = 10,
            token = "123456dasdadfghjasldkfgjhalsdkjfhalksdf",
        )

        var dataResponseReceived: CheckStatusOrderDataResponse? = null
        var dataErrorReceived: ErrorTransaction? = null

        fun onResponse(data: CheckStatusOrderDataResponse?, error: ErrorTransaction?) {
            dataResponseReceived = data
            dataErrorReceived = error
        }

        //WHEN
        handleResponseCheckStatusOrder(orderDataRequest,
            response = mockCallResponseBody.execute(),
            ::onResponse,
            logEventsService = logEventsService,
            logErrorService = logErrorService
        )

        val responseErrorExpected = getGenericErrorData()

        //THEN
        Assert.assertEquals(responseErrorExpected, dataErrorReceived)
        Assert.assertEquals(null, dataResponseReceived)


        //test sentry logs
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair("error_catch_gateway",
                    "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $"),
                Pair("error_generic_gateway",
                    "{\"message\":\"title_unexpected_error\",\"messageDetails\":\"title_unexpected_error_body\",\"errorTags\":\"UX000\"}"),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API | status_code: 200 (api/v2/gateway/status)"
        )

        Assert.assertEquals(listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras)
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)

    }

    @Test
    fun `test Withdraw given all data is null`() {
        val withdraw = Withdraw(
            id = null,
            status_id = null,
            status_name = null
        )

        Assert.assertEquals(null, withdraw.id)
        Assert.assertEquals(null, withdraw.status_id)
        Assert.assertEquals(null, withdraw.status_name)
    }

    @Test
    fun `test CheckStatusOrderResponse given all valid data`() {
        val checkStatusOrderResponse = CheckStatusOrderResponse(
            isLoading = false,
            isSuccess = true,
            data = CheckStatusOrderDataResponse(),
        )

        Assert.assertEquals(false, checkStatusOrderResponse.isLoading)
        Assert.assertEquals(true, checkStatusOrderResponse.isSuccess)
        Assert.assertEquals(CheckStatusOrderDataResponse(), checkStatusOrderResponse.data)
    }

    @Test
    fun `test CheckStatusOrderResponse given some data is null`() {
        val checkStatusOrderResponse = CheckStatusOrderDataResponse(
            error = null,
            operation = null,
            operation_name = null,
            full_name = null,
            document_number = null,
            currency = null,
            converted_amount = null
        )

        Assert.assertEquals(null, checkStatusOrderResponse.error)
        Assert.assertEquals(null, checkStatusOrderResponse.operation)
        Assert.assertEquals(null, checkStatusOrderResponse.operation_name)
        Assert.assertEquals(null, checkStatusOrderResponse.full_name)
        Assert.assertEquals(null, checkStatusOrderResponse.document_number)
        Assert.assertEquals(null, checkStatusOrderResponse.currency)
        Assert.assertEquals(null, checkStatusOrderResponse.converted_amount)
    }

    @Test
    fun `CheckStatusOrderDataResponse given error is not null`(){
        val errorTransaction = ErrorTransaction(message = "error test")
        val checkStatusOrderResponse = CheckStatusOrderDataResponse(
            error = errorTransaction)
        Assert.assertEquals(errorTransaction, checkStatusOrderResponse.error)
    }

}