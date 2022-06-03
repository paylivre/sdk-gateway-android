package com.paylivre.sdk.gateway.android

import android.app.Activity
import android.os.Build
import com.paylivre.sdk.gateway.android.utils.dpToPx
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], qualifiers = "pt-port")
class DpToPxTest {
    private val activityMocked: Activity =
        Robolectric.buildActivity(Activity::class.java).create().get()

    @Before
    fun setupTheme() {
        activityMocked.setTheme(R.style.Theme_MaterialComponents_Light)
    }

    @Test
    fun `test DpToPxTest`() {
        Assert.assertEquals(10f, dpToPx(activityMocked.resources, 10f))
    }
}