package com.paylivre.sdk.gateway.android.data.model.servicesStatus

import org.junit.Assert
import org.junit.Test

class ServiceStatusResponseTest {

    @Test
    fun `CASE 01, instantiating the ServiceStatusResponse class`() {
        // GIVEN
        val wireTransferStatus = DataServiceStatus(Item(status = 1, id = 1))
        val billetStatus = DataServiceStatus(Item(status = 0, id = 2))
        val serviceStatusResponse =
            ServiceStatusResponse(
                status = "success",
                status_code = 200,
                message = "OK",
                data = listOf(wireTransferStatus, billetStatus),
            )

        // THEN
        Assert.assertEquals("OK", serviceStatusResponse.message)
        Assert.assertEquals(DataServiceStatus(service=Item(status=0, id=2)), serviceStatusResponse.data[1])
    }
}