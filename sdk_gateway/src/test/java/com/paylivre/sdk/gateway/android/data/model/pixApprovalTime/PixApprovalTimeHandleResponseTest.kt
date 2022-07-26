package com.paylivre.sdk.gateway.android.data.model.pixApprovalTime

import com.paylivre.sdk.gateway.android.data.MockCallResponseBody
import com.paylivre.sdk.gateway.android.data.RemoteDataSourceTestUtils
import com.paylivre.sdk.gateway.android.services.log.LogErrorServiceImplTest
import org.junit.Assert
import org.junit.Test

class PixApprovalTimeHandleResponseTest {
    @Test
    fun `CASE 01, test getResponseJson given body() is null`() {
        val mockCallResponseBody = MockCallResponseBody(code = 400)
        var isErrorCatch = false

        //WHEN
        try {
            getResponseJson(mockCallResponseBody.execute())
        } catch (e: Exception) {
            isErrorCatch = true
        }

        Assert.assertTrue(isErrorCatch)
    }

    @Test
    fun `handleResponsePixApprovalTime, given StatusOk is not ok`() {
        val rawResponse = "{\"status\":\"error\",\"status_code\":200,\"message\":\"OK\",\"data\":{\"deposit_age_minutes\":30,\"deposit_status_id\":2,\"average_age\":\"00:00:11\",\"level\":\"normal\",\"level_id\":1}}"
        val mockCallResponseBody =
            MockCallResponseBody(code = 200, rawResponseBodySuccess = rawResponse)

        val logErrorService = LogErrorServiceImplTest()
        var dataResponseReceived: PixApprovalTimeResponse? = null
        var dataErrorReceived: Throwable? = null

        fun onResponse(data: PixApprovalTimeResponse?, error: Throwable?) {
            dataResponseReceived = data
            dataErrorReceived = error
        }

        handleResponsePixApprovalTime(
            response = mockCallResponseBody.execute(),
            onResponse = ::onResponse,
            logErrorService = logErrorService
        )

        //THEN
        Assert.assertEquals("title_unexpected_error", dataErrorReceived?.message)
        Assert.assertEquals(null, dataResponseReceived)

        //test sentry logs
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair("response_status", "error"),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API (api/v2/gateway/averagePixApprovalTime)"
        )
        Assert.assertEquals(listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras)

        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)
    }

    @Test
    fun `handleResponsePixApprovalTime, given an error occurs that triggers the catch`() {
        val rawResponseBody = "{\"test_error\":\"test\"}"
        val mockCallResponseBody =
            MockCallResponseBody(code = 200, rawResponseBodySuccess = rawResponseBody)
        val logErrorService = LogErrorServiceImplTest()
        var dataResponseReceived: PixApprovalTimeResponse? = null
        var dataErrorReceived: Throwable? = null

        fun onResponse(data: PixApprovalTimeResponse?, error: Throwable?) {
            dataResponseReceived = data
            dataErrorReceived = error
        }

        handleResponsePixApprovalTime(
            response = mockCallResponseBody.execute(),
            onResponse = ::onResponse,
            logErrorService = logErrorService
        )

        //THEN
        Assert.assertEquals("title_unexpected_error", dataErrorReceived?.message)
        Assert.assertEquals(null, dataResponseReceived)

        //test sentry logs
        val listOfListOfCapturedExtrasExpected =
            RemoteDataSourceTestUtils().getListOfCapturedExtras(
                Pair("error_catch_average_pix_approval_time", "Parameter specified as non-null is null: method com.paylivre.sdk.gateway.android.data.model.pixApprovalTime.PixApprovalTimeHandleResponseKt.checkInternStatusOk, parameter status"),
            )
        val listOfCapturedMessagesExpected = RemoteDataSourceTestUtils().getListOfCapturedMessages(
            "ERROR_API (api/v2/gateway/averagePixApprovalTime)"
        )
        Assert.assertEquals(listOfListOfCapturedExtrasExpected,
            logErrorService.listOfCapturedExtras)
        Assert.assertEquals(listOfCapturedMessagesExpected, logErrorService.listOfCapturedMessages)
    }

    @Test
    fun `PixApprovalTimeResponse, check all data`(){
        val pixApprovalTimeResponse = PixApprovalTimeResponse(
            status = "",
            status_code = 10,
            message = "test",
            data = null
        )

        Assert.assertEquals("", pixApprovalTimeResponse.status)
        Assert.assertEquals(10, pixApprovalTimeResponse.status_code)
        Assert.assertEquals("test", pixApprovalTimeResponse.message)
        Assert.assertEquals(null, pixApprovalTimeResponse.data)
    }

    @Test
    fun `DataPixApprovalTime, check all data`(){
        val dataPixApprovalTime = DataPixApprovalTime(
            deposit_age_minutes = "",
            deposit_status_id = "",
            average_age = "",
            level = "",
            level_id = 0
        )

        Assert.assertEquals("", dataPixApprovalTime.deposit_age_minutes)
        Assert.assertEquals("", dataPixApprovalTime.deposit_status_id)
        Assert.assertEquals("", dataPixApprovalTime.average_age)
        Assert.assertEquals("", dataPixApprovalTime.level)
        Assert.assertEquals(0, dataPixApprovalTime.level_id)
    }
}