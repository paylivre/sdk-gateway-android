package com.paylivre.sdk.gateway.android.data.model.deposit

import com.paylivre.sdk.gateway.android.FileTestsUtils
import com.paylivre.sdk.gateway.android.data.MockCallResponseBody
import com.paylivre.sdk.gateway.android.data.RemoteDataSourceTestUtils
import com.paylivre.sdk.gateway.android.data.getGenericErrorData
import com.paylivre.sdk.gateway.android.data.model.order.*
import com.paylivre.sdk.gateway.android.services.log.LogErrorServiceImplTest
import org.junit.Assert
import org.junit.Test

class CheckStatusDepositHandleResponseTest {
    @Test
    fun `CASE 01, test getResponseJson given body() is null`() {
        // mocking with 'code = 400' to provoke a errorResponse, which comes with an empty body
        val mockCallResponseBody = MockCallResponseBody(400)
        var isErrorCatch = false

        // WHEN
        try {
            getResponseJson(mockCallResponseBody.execute())
        } catch (e: Exception) {
            isErrorCatch = true
        }

        // THEN
        Assert.assertTrue(isErrorCatch)
    }

    @Test
    fun `CASE 02, test getErrorResponseJson given a null errorBody()`() {
        val mockCallResponseBody = MockCallResponseBody(
            200,
        )
        var isErrorCatch = false

        // WHEN
        try {
            getErrorResponseJson(mockCallResponseBody.execute())
        } catch (e: Exception) {
            isErrorCatch = true
        }

        // THEN
        Assert.assertTrue(isErrorCatch)
    }

    @Test
    fun `CASE 02b, test getErrorResponseJson given a valid errorBody()`() {
        val mockCallResponseBody = MockCallResponseBody(
            400,
            rawResponseBodyError = "{\"message\":\"Houve um erro ao finalizar essa operação!\"}"
        )

        // WHEN
        val receivedError = getErrorResponseJson(mockCallResponseBody.execute())
        val expectedError = ErrorTransaction(
            message = "Houve um erro ao finalizar essa operação!"
        )

        // THEN
        Assert.assertEquals(expectedError, receivedError)
    }

    @Test
    fun `CASE 03, handleResponseDepositCheckStatus, given request isSuccessful and deposit status in response is not success`() {
        // GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val checkStatusDepositResponseDataString =
            FileTestsUtils().loadJsonAsString("res_deposit_status_failure.json")
        val mockCallResponseBody = MockCallResponseBody(
            code = 200,
            rawResponseBodySuccess = checkStatusDepositResponseDataString
        )

        var receivedResponse: CheckStatusDepositResponse? = null
        var receivedError: ErrorTransaction? = null
        fun onResponse(data: CheckStatusDepositResponse?, error: ErrorTransaction?) {
            receivedResponse = data
            receivedError = error
        }

        // EXPECTING
        val expectedError = getGenericErrorData()
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair(
                    "request_url_status_deposit",
                    "(/api/v2/transaction/deposit/status/1)"
                ),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API | status_code: 200 (api/v2/transaction/deposit/status/{id})"
        )

        // WHEN
        handleResponseDepositCheckStatus(
            response = mockCallResponseBody.execute(),
            ::onResponse,
            depositId = 1,
            logErrorService = logErrorService
        )

        // THEN
        Assert.assertEquals(expectedError, receivedError)
        Assert.assertEquals(null, receivedResponse)
        Assert.assertEquals(
            listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras
        )
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)
    }

    @Test
    fun `CASE 04, handleResponseDepositCheckStatus, given catch error`() {
        // GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val mockCallResponseBody = MockCallResponseBody(
            code = 200,
            rawResponseBodySuccess = "invalid_body"
        )

        var receivedResponse: CheckStatusDepositResponse? = null
        var receivedError: ErrorTransaction? = null
        fun onResponse(data: CheckStatusDepositResponse?, error: ErrorTransaction?) {
            receivedResponse = data
            receivedError = error
        }

        // EXPECTING
        val expectedError = ErrorTransaction(
            message = "Houve um erro ao finalizar essa operação!",
            messageDetails = null,
            error = null,
            errors = null,
            errorTags = null,
            original_message = null,
        )
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair(
                    "error_catch_status_deposit",
                    "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$"
                ),
                Pair("request_url_status_deposit", "(/api/v2/transaction/deposit/status/1)"),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API | status_code: 200 (api/v2/transaction/deposit/status/{id})"
        )

        // WHEN
        handleResponseDepositCheckStatus(
            response = mockCallResponseBody.execute(),
            ::onResponse,
            depositId = 1,
            logErrorService = logErrorService
        )

        // THEN
        Assert.assertEquals(expectedError, receivedError)
        Assert.assertEquals(null, receivedResponse)
        Assert.assertEquals(
            listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras
        )
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)

    }
}