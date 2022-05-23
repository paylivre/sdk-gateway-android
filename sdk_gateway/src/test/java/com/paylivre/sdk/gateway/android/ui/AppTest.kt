package com.paylivre.sdk.gateway.android.ui

import android.os.Build
import com.paylivre.sdk.gateway.android.App.Companion.getHostAPI
import com.paylivre.sdk.gateway.android.App.Companion.setHostAPI
import com.paylivre.sdk.gateway.android.utils.API_HOST_ENVIRONMENT_DEV
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], qualifiers = "pt-port")
class AppTest {

    @Before
    fun setDevHostApi() {
        setHostAPI(API_HOST_ENVIRONMENT_DEV)
    }

    @Test
    fun `CASE 1, test getHostAPI DEV`() {
        val hostAPI = getHostAPI()
        Assert.assertEquals("api.dev.paylivre.com", hostAPI)
    }

}