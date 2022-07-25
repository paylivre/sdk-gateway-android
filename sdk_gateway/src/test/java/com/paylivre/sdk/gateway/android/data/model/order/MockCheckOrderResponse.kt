package com.paylivre.sdk.gateway.android.data.model.order

class MockCheckOrderResponse {
    companion object {
        val checkOrderResponseOrderNull = "{\n" +
                "  \"operation\":5,\n" +
                "  \"operation_name\":\"Withdrawal\",\n" +
                "  \"full_name\":\"User Gateway Test\",\n" +
                "  \"document_number\":\"61317581075\",\n" +
                "  \"final_amount\":500,\n" +
                "  \"original_amount\":500,\n" +
                "  \"original_currency\":\"BRL\",\n" +
                "  \"currency\":\"BRL\",\n" +
                "  \"converted_amount\":500,\n" +
                "  \"taxes\":null,\n" +
                "  \"kyc_limits\":{\n" +
                "    \"available_amount\":9999999926808,\n" +
                "    \"limit\":9999999999999,\n" +
                "    \"used_limit\":73191,\n" +
                "    \"kyc_level\":\"3\",\n" +
                "    \"kyc_level_name\":\"Gold\"\n" +
                "  },\n" +
                "  \"withdrawal_type_id\":4,\n" +
                "  \"withdrawal\":{\n" +
                "    \"id\":16741,\n" +
                "    \"status_id\":0,\n" +
                "    \"status_name\":\"New\"\n" +
                "  }\n" +
                "}"
    }
}