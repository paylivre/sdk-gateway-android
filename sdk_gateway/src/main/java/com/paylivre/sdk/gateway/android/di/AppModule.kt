package com.paylivre.sdk.gateway.android.di

import com.paylivre.sdk.gateway.android.data.PaymentRepository
import com.paylivre.sdk.gateway.android.data.api.RemoteDataSource
import com.paylivre.sdk.gateway.android.data.api.services
import com.paylivre.sdk.gateway.android.services.log.LogEventsService
import com.paylivre.sdk.gateway.android.services.log.LogEventsServiceImpl
import com.paylivre.sdk.gateway.android.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LogEventsService> {
        LogEventsServiceImpl.Companion
    }
    single{
        services()
    }
    single{
        RemoteDataSource(get(), get())
    }
    single{
        PaymentRepository(get())
    }
    viewModel {
        MainViewModel(get())
    }
}