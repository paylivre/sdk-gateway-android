package com.paylivre.sdk.gateway.android.data.model.pixApprovalTime

import com.google.gson.Gson
import com.paylivre.sdk.gateway.android.data.api.addSentryBreadcrumb
import com.paylivre.sdk.gateway.android.data.getGenericErrorData
import com.paylivre.sdk.gateway.android.services.log.LogErrorService
import com.paylivre.sdk.gateway.android.services.log.LogErrorServiceImpl
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Exception
import java.lang.RuntimeException

fun checkInternStatusOk(status: String): Boolean {
    return status == "success"
}

fun getResponseJson(response: Response<ResponseBody>): PixApprovalTimeResponse {
    val message = response.body()?.string()
    //Log error Sentry
    addSentryBreadcrumb("original_response_average_pix_approval_time",
        message.toString())

    return Gson().fromJson(message, PixApprovalTimeResponse::class.java)
}


fun handleResponsePixApprovalTime(
    response: Response<ResponseBody>,
    onResponse: (PixApprovalTimeResponse?, Throwable?) -> Unit,
    logErrorService: LogErrorService = LogErrorServiceImpl(),
) {
    try {
        if (response.isSuccessful) {
            val res: PixApprovalTimeResponse = getResponseJson(response)

            if (!checkInternStatusOk(res.status)) {
                val message = getGenericErrorData().message
                onResponse(null, RuntimeException(message))

                //Sentry log errors
                logErrorService.setExtra("response_status", res.status)
                logErrorService.captureMessage("ERROR_API (api/v2/gateway/averagePixApprovalTime)")
            } else {
                onResponse(res, null)
            }

        } else {
            val message = getGenericErrorData().message
            onResponse(null, RuntimeException(message))

            //Sentry log errors
            logErrorService.setExtra("original_response_error_average_pix_approval_time", response.errorBody().toString())
            logErrorService.captureMessage("ERROR_API (api/v2/gateway/averagePixApprovalTime)")

        }

    } catch (err: Exception) {
        onResponse(null, RuntimeException(getGenericErrorData().message))

        //Sentry log errors
        logErrorService.setExtra("error_catch_average_pix_approval_time", err.message.toString())
        logErrorService.captureMessage("ERROR_API (api/v2/gateway/averagePixApprovalTime)")
    }
}
