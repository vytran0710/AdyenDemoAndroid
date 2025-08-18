package com.example.adyendemo.module

import com.example.adyendemo.feature.payment_method_selection.data.data_source.PaymentLocalDataSource
import com.example.adyendemo.feature.payment_method_selection.data.data_source.PaymentRemoteDataSource
import com.example.adyendemo.feature.payment_method_selection.domain.repository.PaymentRepository
import com.example.adyendemo.feature.payment_method_selection.presentation.view_model.PaymentDetailViewModel
import com.example.adyendemo.feature.payment_method_selection.presentation.view_model.PaymentMethodSelectionViewModel
import com.example.adyendemo.module.network.NetworkClient
import com.example.adyendemo.module.network.NetworkService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //
    factory<Gson> {
        GsonBuilder().setLenient().create()
    }
    //
    factory<NetworkService> {
        NetworkClient.create(gson = get())
    }

    //
    factory<PaymentRemoteDataSource> {
        PaymentRemoteDataSource(networkClient = get())
    }

    //
    factory<PaymentLocalDataSource> {
        PaymentLocalDataSource()
    }

    //
    factory<PaymentRepository> {
        PaymentRepository(paymentRemoteDataSource = get(), paymentLocalDataSource = get())
    }

    //
    viewModel {
        PaymentMethodSelectionViewModel(paymentRepository = get())
    }

    //
    viewModel {
        PaymentDetailViewModel(paymentRepository = get())
    }
}