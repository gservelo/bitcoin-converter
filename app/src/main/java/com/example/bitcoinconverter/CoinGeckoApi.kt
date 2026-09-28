package com.example.bitcoinconverter

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("simple/price")
    suspend fun getBitcoinPrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrencies: String = "brl"
    ): Response<CoinGeckoResponse>
}
