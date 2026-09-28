package com.example.bitcoinconverter

import com.google.gson.annotations.SerializedName

// Resposta da API CoinGecko
data class CoinGeckoResponse(
    @SerializedName("bitcoin")
    val bitcoin: BitcoinPrice
)

data class BitcoinPrice(
    @SerializedName("brl")
    val brl: Double
)

// Item do histórico de conversões
data class HistoricoItem(
    val entrada: String,
    val resultado: String,
    val cotacao: String,
    val timestamp: String
)
