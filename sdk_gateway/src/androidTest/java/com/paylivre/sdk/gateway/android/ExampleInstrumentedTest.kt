package com.paylivre.sdk.gateway.android

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paylivre.sdk.gateway.android.ui.form.FormStartPaymentFragment
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun testEventFragment() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<FormStartPaymentFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)
        fragment.onFragment {
            //GIVEN
            val textViewTitleForm = it.view?.findViewById<TextView>(R.id.textViewTitleForm)
            val editEmail = it.view?.findViewById<EditText>(R.id.editEmail)

            //WHEN
            editEmail?.setText("test@test.com")

            //THEN
            Assert.assertEquals("Fill in the fields below to continue",
                textViewTitleForm?.text.toString())

            Assert.assertEquals("test@test.com", editEmail?.text.toString())
        }
    }
}