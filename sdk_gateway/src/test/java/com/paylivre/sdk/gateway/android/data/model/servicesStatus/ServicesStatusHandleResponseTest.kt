package com.paylivre.sdk.gateway.android.data.model.servicesStatus

import com.paylivre.sdk.gateway.android.FileTestsUtils
import com.paylivre.sdk.gateway.android.data.MockCallResponseBody
import com.paylivre.sdk.gateway.android.data.RemoteDataSourceTestUtils
import com.paylivre.sdk.gateway.android.data.getGenericErrorData

import com.paylivre.sdk.gateway.android.services.log.LogErrorServiceImplTest
import org.junit.Assert
import org.junit.Test
import java.lang.RuntimeException

class ServicesStatusHandleResponseTest {
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
    fun `CASE 02, handleResponseServiceStatus, given request isSuccessful and deposit status in response is not success`() {
        // GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val checkStatusDepositResponseDataString =
            FileTestsUtils().loadJsonAsString("response_get_services_status_failure.json")
        val mockCallResponseBody = MockCallResponseBody(
            code = 200,
            rawResponseBodySuccess = checkStatusDepositResponseDataString
        )

        var receivedAdapter: ServiceStatusResponseAdapter? = null
        var receivedThrowable: Throwable? = null
        fun onResponse(adapter: ServiceStatusResponseAdapter?, throwable: Throwable?) {
            receivedAdapter = adapter
            receivedThrowable = throwable
        }

        // EXPECTING
        val expectedThrowable = RuntimeException("title_unexpected_error")
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair(
                    "request_error_deposit_status",
                    "null"
                ),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API | status_code: 200 (api/v2/gateway/deposit-status)"
        )

        // WHEN
        handleResponseServiceStatus(
            response = mockCallResponseBody.execute(),
            ::onResponse,
            logErrorService = logErrorService
        )

        // THEN
        Assert.assertEquals(expectedThrowable.message, receivedThrowable?.message)
        Assert.assertEquals(null, receivedAdapter)
        Assert.assertEquals(
            listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras
        )
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)
    }

    @Test
    fun `CASE 03, handleResponseServiceStatus, given catch error`() {
        // GIVEN
        val logErrorService = LogErrorServiceImplTest()
        val mockCallResponseBody = MockCallResponseBody(
            code = 200,
            rawResponseBodySuccess = "invalid_body"
        )

        var receivedAdapter: ServiceStatusResponseAdapter? = null
        var receivedThrowable: Throwable? = null
        fun onResponse(adapter: ServiceStatusResponseAdapter?, throwable: Throwable?) {
            receivedAdapter = adapter
            receivedThrowable = throwable
        }

        // EXPECTING
        val expectedThrowable = RuntimeException("title_unexpected_error")
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair(
                    "error_catch_deposit_status",
                    "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$"
                ),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API | status_code: 200 (api/v2/gateway/deposit-status)"
        )

        // WHEN
        handleResponseServiceStatus(
            response = mockCallResponseBody.execute(),
            ::onResponse,
            logErrorService = logErrorService
        )

        // THEN
        Assert.assertEquals(expectedThrowable.message, receivedThrowable?.message)
        Assert.assertEquals(null, receivedAdapter)
        Assert.assertEquals(
            listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras
        )
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)
    }
}
