package com.paylivre.sdk.gateway.android.ui.transactions.finishscreen.deposit.wiretransfer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.gson.Gson
import com.paylivre.sdk.gateway.android.FileTestsUtils
import com.paylivre.sdk.gateway.android.R
import com.paylivre.sdk.gateway.android.data.model.order.ResponseCommonTransactionData
import com.paylivre.sdk.gateway.android.data.model.order.StatusTransactionResponse
import com.paylivre.sdk.gateway.android.data.model.transferProof.InsertTransferProofDataResponse
import com.paylivre.sdk.gateway.android.domain.model.OriginTypeInsertProof
import com.paylivre.sdk.gateway.android.getOrAwaitValueTest
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], qualifiers = "pt-port")
class InsertProofWireTransferFragmentTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var fileTestsUtils = FileTestsUtils()

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
    fun `CASE 01, inserted the proof`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<InsertProofWireTransferFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { insertProofWireTransferFragment ->
            //GIVEN
            val mockStatusTransactionResponse = getMockStatusTransactionResponseSuccess()
            insertProofWireTransferFragment.mainViewModel.setStatusTransactionResponse(
                mockStatusTransactionResponse
            )

            //WHEN
            insertProofWireTransferFragment.mainViewModel.setOriginTypeInsertProof(
                OriginTypeInsertProof.CAMERA
            )

            insertProofWireTransferFragment.mainViewModel.setOriginTypeInsertProof(
                OriginTypeInsertProof.GALLERY
            )

            val mockIntent = Intent()
            val uri: Uri =
                Uri.parse("file:///storage/emulated/0/Android/data/com.example.paylivre.sdk.gateway/files/DCIM/IMG_20220603_001948249.jpg")
            mockIntent.data = uri
            insertProofWireTransferFragment.handleImagePickerSuccess(mockIntent)

            //THEN
            //Check data containerInsertProof
            Assert.assertEquals("Comprovante de depósito",
                insertProofWireTransferFragment.view?.findViewById<TextView>(R.id.textTitleDepositProof)?.text.toString())
            Assert.assertEquals("Nenhum arquivo selecionado",
                insertProofWireTransferFragment.view?.findViewById<TextView>(R.id.textNameSelectedFile)?.text.toString())
            Assert.assertEquals(View.VISIBLE,
                insertProofWireTransferFragment.view?.findViewById<ConstraintLayout>(R.id.btnChooseFile)?.visibility)
            Assert.assertEquals("Alterar arquivo escolhido",
                insertProofWireTransferFragment.view?.findViewById<TextView>(R.id.txtChooseFile)?.text.toString())
            Assert.assertEquals("O arquivo deve ser do tipo: jpg, png, jpeg e tamanho máximo de 5MB.",
                insertProofWireTransferFragment.view?.findViewById<TextView>(R.id.textInstructionFile)?.text.toString())

            //Button submit
            Assert.assertEquals(View.VISIBLE,
                insertProofWireTransferFragment.view?.findViewById<Button>(R.id.btnSubmit)?.visibility)
            Assert.assertEquals(true,
                insertProofWireTransferFragment.view?.findViewById<Button>(R.id.btnSubmit)?.isEnabled)
            Assert.assertEquals("Enviar",
                insertProofWireTransferFragment.view?.findViewById<Button>(R.id.btnSubmit)?.text.toString())
        }
    }

    @Test
    fun `CASE 02, setProofImageUri given Uri is null`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<InsertProofWireTransferFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { insertProofWireTransferFragment ->
            insertProofWireTransferFragment.mainViewModel.setProofImageUri(null)
        }
    }

    @Test
    fun `CASE 03, test btnChooseFile`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<InsertProofWireTransferFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { insertProofWireTransferFragment ->
            val btnChooseFile =
                insertProofWireTransferFragment.view?.findViewById<ConstraintLayout>(R.id.btnChooseFile)

            btnChooseFile?.performClick()

            //To run childFragmentManager transactions
            insertProofWireTransferFragment.childFragmentManager.executePendingTransactions()
        }
    }

    @Test
    fun `CASE 03, test btnSubmit`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<InsertProofWireTransferFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { insertProofWireTransferFragment ->
            //GIVEN
            val mockStatusTransactionResponse = getMockStatusTransactionResponseSuccess()
            insertProofWireTransferFragment.mainViewModel.setStatusTransactionResponse(
                mockStatusTransactionResponse
            )

            val mockIntent = Intent()
            val uri: Uri =
                Uri.parse("file:///storage/emulated/0/Android/data/com.example.paylivre.sdk.gateway/files/DCIM/IMG_20220603_001948249.jpg")
            mockIntent.data = uri
            insertProofWireTransferFragment.handleImagePickerSuccess(mockIntent)
            val btnSubmit =
                insertProofWireTransferFragment.view?.findViewById<Button>(R.id.btnSubmit)

            //THEN
            btnSubmit?.performClick()
            //To run childFragmentManager transactions
            insertProofWireTransferFragment.childFragmentManager.executePendingTransactions()

            Assert.assertEquals(InsertTransferProofDataResponse(
                id = null,
                proof = null,
                wallet_id = null,
                user_id = null,
                deposit_status_id = null,
                loading = true,
                error = null,
                isSuccess = null
            ),
                insertProofWireTransferFragment.mainViewModel.transfer_proof_response.getOrAwaitValueTest())
        }
    }

    @Test
    fun `CASE 04, test handleImagePicker`() {
        val fragmentArgs = Bundle()
        val fragment = launchFragmentInContainer<InsertProofWireTransferFragment>(fragmentArgs,
            themeResId = R.style.Theme_SDKGatewayAndroid)

        fragment.onFragment { insertProofWireTransferFragment ->
            insertProofWireTransferFragment.handleImagePickerError(null)
            insertProofWireTransferFragment.handleImagePickerCancelled()
        }
    }

}