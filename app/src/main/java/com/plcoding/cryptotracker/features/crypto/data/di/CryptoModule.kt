package com.plcoding.cryptotracker.features.crypto.data.di

import com.plcoding.cryptotracker.features.crypto.data.networking.HttpClientFactory
import com.plcoding.cryptotracker.features.crypto.data.source.remote.KtorCoinsRemoteDatasource
import com.plcoding.cryptotracker.features.crypto.domain.source.CoinRemoteDataSource
import com.plcoding.cryptotracker.features.crypto.presentation.viewmodel.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val cryptoModule = module {


    single {
        HttpClientFactory.create(
            CIO.create(),
        )
    }
    single<CoinRemoteDataSource> {

        KtorCoinsRemoteDatasource(get())
    }


    viewModelOf(::CoinListViewModel)
}