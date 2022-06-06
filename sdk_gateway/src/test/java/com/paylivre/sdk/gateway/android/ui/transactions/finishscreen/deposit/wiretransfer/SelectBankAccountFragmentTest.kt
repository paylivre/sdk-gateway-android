package com.paylivre.sdk.gateway.android.ui.transactions.finishscreen.deposit.wiretransfer

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.gson.Gson
import com.paylivre.sdk.gateway.android.FileTestsUtils
import com.paylivre.sdk.gateway.android.R
import com.paylivre.sdk.gateway.android.data.model.order.ResponseCommonTransactionData
import com.paylivre.sdk.gateway.android.data.model.order.StatusTransactionResponse
import com.paylivre.sdk.gateway.android.viewmodel.MockMainViewModel
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], qualifiers = "pt-port")
class SelectBankAccountFragmentTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var fileTestsUtils = FileTestsUtils()

    @Before
    fun setup() {
        loadKoinModules(MockMainViewModel().mockedAppModule)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    private fun getMockStatusTransactionResponseSuccess(): StatusTransactionResponse {
        val responseExpectedString =
            fileTestsUtils.loadJsonAsString("res_deposit_wiretransfer_success.json")

        val expectedDataResponse = Gson().fromJson(
            responseExpectedString, ResponseCommonTransactionData::class.java
        )

        return StatusTransactionResponse(
            isLoading = false,
            isSuccess = true,
            data = expectedDataResponse,
            error = null
        )
    }


    @Test
    fun `CASE 01, SelectBankAccountFragment`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<SelectBankAccountFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { selectBankAccountFragment ->
            //GIVEN
            val mockStatusTransactionResponse = getMockStatusTransactionResponseSuccess()
            selectBankAccountFragment.mainViewModel.setStatusTransactionResponse(
                mockStatusTransactionResponse
            )


            //WHEN
            //Selected bank
            val textViewBankInfo =
                selectBankAccountFragment.view?.findViewById<TextView>(R.id.textViewBankInfo)
            val mockBankPosition = 0
            val banksList = mockStatusTransactionResponse.data?.bank_accounts
            val selectedItemText = banksList?.elementAt(mockBankPosition)
            val infoSelectedBankText = selectedItemText?.let {
                getBankAccountInfo(selectBankAccountFragment.requireContext(), it)
            }
            selectBankAccountFragment.setOnBankClick(mockBankPosition, banksList)


            //THEN
            Assert.assertEquals(infoSelectedBankText, textViewBankInfo?.text.toString())
        }
    }

    @Test
    fun `CASE 02, test clearAllFocus`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<SelectBankAccountFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { selectBankAccountFragment ->
            selectBankAccountFragment.mainViewModel.setClearAllFocus(true)
        }
    }
}