package com.paylivre.sdk.gateway.android.data.model.pixApprovalTime

import org.junit.Assert
import org.junit.Test
import com.paylivre.sdk.gateway.android.R


class GetDataPixApprovalTimeResponseTest {

    @Test
    fun `getFlagColorId, given level is normal`(){
        Assert.assertEquals( R.color.success_color_paylivre_sdk, getFlagColorId("normal"))
    }

    @Test
    fun `getFlagColorId, given level is slow`(){
        Assert.assertEquals( R.color.warning2_color_paylivre_sdk, getFlagColorId("slow"))
    }

    @Test
    fun `getFlagColorId, given level is other`(){
        Assert.assertEquals( R.color.danger_color_paylivre_sdk, getFlagColorId("other"))
    }

    @Test
    fun `getLevelKey, given level is normal`(){
        Assert.assertEquals( "level_status_pix_time_normal", getLevelKey("normal"))
    }

    @Test
    fun `getLevelKey, given level is slow`(){
        Assert.assertEquals( "level_status_pix_time_slow", getLevelKey("slow"))
    }

    @Test
    fun `getLevelKey, given level is other`(){
        Assert.assertEquals("level_status_pix_time_very_slow", getLevelKey("other"))
    }

    @Test
    fun `getTime, given average_age is valid string`(){
        val responseGetTimeExpected = ResponseGetTime(
            average_time_seconds = 11,
            average_time_minutes = 1,
            average_time_hours = 1
        )
        Assert.assertEquals(responseGetTimeExpected, getTime("01:01:11"))
    }


    @Test
    fun `getAverageTimeKey, given average_time_hours is greater than 0`(){
        val mockResponseGetTime = ResponseGetTime(
            average_time_hours = 1,
           average_time_minutes = 23,
            average_time_seconds = 55,
        )
        val dataAverageTimeKeyExpected = ResponseGetAverageTimeKey(
            "average_time_hours_minutes",
            1,
            23
        )
        Assert.assertEquals( dataAverageTimeKeyExpected, getAverageTimeKey(mockResponseGetTime))
    }

    @Test
    fun `getAverageTimeKey, given average_time_hours and average_time_minutes are not greater than 0`(){
        val mockResponseGetTime = ResponseGetTime(
            average_time_hours = 0,
            average_time_minutes = 0,
            average_time_seconds = 55,
        )
        val dataAverageTimeKeyExpected = ResponseGetAverageTimeKey(
            "average_time_seconds",
            55,
            55
        )
        Assert.assertEquals( dataAverageTimeKeyExpected, getAverageTimeKey(mockResponseGetTime))
    }

    @Test
    fun `CASE 01, ResponseGetDataAveragePixApprovalTime`(){
        val responseGetDataAveragePixApprovalTime = ResponseGetDataAveragePixApprovalTime(
            average_time_key = "",
            average_unit_time_1 = 0,
            average_unit_time_2 = 0,
            level_key = "",
            flag_color_id = 0
        )

        Assert.assertEquals("", responseGetDataAveragePixApprovalTime.average_time_key)
        Assert.assertEquals(0, responseGetDataAveragePixApprovalTime.average_unit_time_1)
        Assert.assertEquals(0, responseGetDataAveragePixApprovalTime.average_unit_time_2)
        Assert.assertEquals("", responseGetDataAveragePixApprovalTime.level_key)
        Assert.assertEquals(0, responseGetDataAveragePixApprovalTime.flag_color_id)
    }

    @Test
    fun `ResponseGetTime case all values is zero`(){
        val responseGetTime = ResponseGetTime(0,0,0)
        Assert.assertEquals(0, responseGetTime.average_time_hours)
        Assert.assertEquals(0, responseGetTime.average_time_minutes)
        Assert.assertEquals(0, responseGetTime.average_time_seconds)
    }
}