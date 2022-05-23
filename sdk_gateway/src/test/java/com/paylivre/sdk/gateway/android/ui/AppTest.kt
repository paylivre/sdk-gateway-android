package com.paylivre.sdk.gateway.android.ui

import android.os.Build
import com.paylivre.sdk.gateway.android.App.Companion.getHostAPI
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], qualifiers = "pt-port")
class AppTest {

    @Test
    fun `CASE 1, test getHostAPI`() {
        val hostAPI = getHostAPI()
        Assert.assertNull(hostAPI)
    }
}