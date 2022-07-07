package com.paylivre.sdk.gateway.android.services.countdowntimer

import android.os.CountDownTimer


class CountDownTimerServiceImpl : CountDownTimerService {
    override lateinit var countDownTimer: CountDownTimer
    var timerFinalCheckInMillisMocked: Long = 0
    var timerIntervalInMillisMocked: Long = 0

    override fun startTimer(
        timerFinalCheckInMillis: Long,
        timerIntervalInMillis: Long,
        onTickAction: (Long) -> Unit,
        onFinishAction: () -> Unit,
    ) {
        countDownTimer = object : CountDownTimer(timerFinalCheckInMillisMocked,
            timerIntervalInMillisMocked) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                onTickAction(seconds)
            }

            override fun onFinish() {
                onFinishAction()
            }
        }

        countDownTimer.start()
    }

    override fun cancel() {
        countDownTimer.cancel()
    }

}

class MockCountDownTimerGivenHelper :
    CountDownTimerService {
    override lateinit var countDownTimer: CountDownTimer
    var timerFinalCheckInMillisMocked: Long = 10
    var timerIntervalInMillisMocked: Long = 10
    private lateinit var _onTickAction: (Long) -> Unit
    private lateinit var _onFinishAction: () -> Unit
    var calledCancel = false

    override fun startTimer(
        timerFinalCheckInMillis: Long,
        timerIntervalInMillis: Long,
        onTickAction: (Long) -> Unit,
        onFinishAction: () -> Unit,
    ) {
        countDownTimer = object : CountDownTimer(timerFinalCheckInMillisMocked,
            timerIntervalInMillisMocked) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                onTickAction(seconds)
            }

            override fun onFinish() {
                onFinishAction()
            }
        }

        _onFinishAction = onFinishAction
        _onTickAction = onTickAction

        countDownTimer.start()
    }

    override fun cancel() {
        calledCancel = true
        countDownTimer.cancel()
    }

    fun dispatchOnFinish() {
        _onFinishAction()
    }

    fun dispatchOnTick(seconds: Long) {
        _onTickAction(seconds)
    }

}