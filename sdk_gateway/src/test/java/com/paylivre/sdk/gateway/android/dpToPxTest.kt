package com.paylivre.sdk.gateway.android


import android.os.Build
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import com.paylivre.sdk.gateway.android.ui.header.HeaderFragment
import com.paylivre.sdk.gateway.android.utils.dpToPx
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], qualifiers = "pt-port")
class DpToPxTest {

    @Test
    fun `test DpToPxTest`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<HeaderFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment{
            Assert.assertEquals(10f, dpToPx(it.resources, 10f))
        }
    }
}