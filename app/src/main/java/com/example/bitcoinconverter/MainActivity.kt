package com.example.bitcoinconverter

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bitcoinconverter.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Cotação atual do Bitcoin em BRL
    private var cotacaoBtcBrl: Double = 0.0

    // true = R$ → BTC | false = BTC → R$
    private var convertendoRealParaBtc: Boolean = true

    // Formatadores
    private val formatadorReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private val formatadorBtc = NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
        minimumFractionDigits = 8
        maximumFractionDigits = 8
    }
    private val formatadorHora = SimpleDateFormat("HH:mm:ss", Locale("pt", "BR"))

    // Lista de histórico
    private val historico = mutableListOf<HistoricoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarListeners()
        buscarCotacaoOnline()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Configuração dos listeners
    // ─────────────────────────────────────────────────────────────────────────

    private fun configurarListeners() {

        // Radio: modo da cotação (Auto / Manual)
        binding.rgModoCotacao.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCotacaoAuto -> {
                    binding.tilCotacaoManual.visibility = View.GONE
                    buscarCotacaoOnline()
                }
                R.id.rbCotacaoManual -> {
                    binding.tilCotacaoManual.visibility = View.VISIBLE
                }
            }
        }

        // Botão: atualizar cotação
        binding.btnAtualizarCotacao.setOnClickListener {
            if (binding.rbCotacaoAuto.isChecked) {
                buscarCotacaoOnline()
            } else {
                usarCotacaoManual()
            }
        }

        // Radio: modo de conversão (BRL→BTC / BTC→BRL)
        binding.rgModoConversao.setOnCheckedChangeListener { _, checkedId ->
            convertendoRealParaBtc = (checkedId == R.id.rbRealParaBtc)
            atualizarHintEntrada()
            // Limpa resultado ao trocar direção
            binding.cardResultado.visibility = View.GONE
            binding.etEntrada.text?.clear()
        }

        // Botão: converter
        binding.btnConverter.setOnClickListener {
            realizarConversao()
        }

        // Botão: limpar histórico
        binding.btnLimparHistorico.setOnClickListener {
            historico.clear()
            binding.llHistorico.removeAllViews()
            binding.cardHistorico.visibility = View.GONE
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cotação
    // ─────────────────────────────────────────────────────────────────────────

    private fun buscarCotacaoOnline() {
        binding.tvCotacaoAtual.text = getString(R.string.carregando_cotacao)
        binding.btnAtualizarCotacao.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getBitcoinPrice()
                if (response.isSuccessful && response.body() != null) {
                    cotacaoBtcBrl = response.body()!!.bitcoin.brl
                    exibirCotacao(cotacaoBtcBrl, online = true)
                } else {
                    mostrarErroCotacao()
                }
            } catch (e: Exception) {
                mostrarErroCotacao()
            } finally {
                binding.btnAtualizarCotacao.isEnabled = true
            }
        }
    }

    private fun usarCotacaoManual() {
        val texto = binding.etCotacaoManual.text.toString().trim()
        if (texto.isEmpty()) {
            binding.tilCotacaoManual.error = "Insira a cotação"
            return
        }
        binding.tilCotacaoManual.error = null
        val valor = texto.replace(",", ".").toDoubleOrNull()
        if (valor == null || valor <= 0) {
            binding.tilCotacaoManual.error = "Cotação inválida"
            return
        }
        cotacaoBtcBrl = valor
        exibirCotacao(cotacaoBtcBrl, online = false)
    }

    private fun exibirCotacao(valor: Double, online: Boolean) {
        val valorFormatado = formatadorReal.format(valor)
        binding.tvCotacaoAtual.text = getString(R.string.cotacao_label, valorFormatado)
        if (online) {
            Toast.makeText(this, "Cotação atualizada: $valorFormatado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarErroCotacao() {
        binding.tvCotacaoAtual.text = getString(R.string.erro_cotacao)
        Toast.makeText(this, "Sem conexão. Use cotação manual.", Toast.LENGTH_LONG).show()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Conversão
    // ─────────────────────────────────────────────────────────────────────────

    private fun realizarConversao() {
        val textoEntrada = binding.etEntrada.text.toString().trim()

        if (textoEntrada.isEmpty()) {
            binding.tilEntrada.error = getString(R.string.erro_campo_vazio)
            return
        }
        binding.tilEntrada.error = null

        if (cotacaoBtcBrl <= 0) {
            Toast.makeText(this, getString(R.string.erro_sem_cotacao), Toast.LENGTH_LONG).show()
            return
        }

        val valorEntrada = textoEntrada.replace(",", ".").toDoubleOrNull()
        if (valorEntrada == null || valorEntrada <= 0) {
            binding.tilEntrada.error = "Valor inválido"
            return
        }

        val (entradaLabel, resultadoLabel, resultadoFormatado) = if (convertendoRealParaBtc) {
            val btc = valorEntrada / cotacaoBtcBrl
            Triple(
                formatadorReal.format(valorEntrada),
                "₿ ${formatadorBtc.format(btc)}",
                btc
            )
        } else {
            val brl = valorEntrada * cotacaoBtcBrl
            Triple(
                "₿ ${formatadorBtc.format(valorEntrada)}",
                formatadorReal.format(brl),
                brl
            )
        }

        // Exibe resultado
        binding.tvResultado.text = resultadoLabel
        val direcao = if (convertendoRealParaBtc) "R$ → BTC" else "BTC → R$"
        binding.tvDetalheConversao.text = "$direcao · Cotação: ${formatadorReal.format(cotacaoBtcBrl)}"
        binding.cardResultado.visibility = View.VISIBLE

        // Adiciona ao histórico
        adicionarHistorico(
            entrada = entradaLabel,
            resultado = resultadoLabel,
            cotacao = formatadorReal.format(cotacaoBtcBrl)
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Histórico
    // ─────────────────────────────────────────────────────────────────────────

    private fun adicionarHistorico(entrada: String, resultado: String, cotacao: String) {
        val hora = formatadorHora.format(Date())
        val item = HistoricoItem(entrada, resultado, cotacao, hora)
        historico.add(0, item)

        // Máximo 5 itens
        if (historico.size > 5) historico.removeAt(historico.size - 1)

        atualizarListaHistorico()
        binding.cardHistorico.visibility = View.VISIBLE
    }

    private fun atualizarListaHistorico() {
        binding.llHistorico.removeAllViews()
        historico.forEach { item ->
            val tv = TextView(this).apply {
                text = "${item.timestamp}  ${item.entrada} → ${item.resultado}"
                textSize = 13f
                setPadding(0, 8, 0, 8)
                setTextColor(getColor(R.color.text_primary))
            }
            binding.llHistorico.addView(tv)

            // Divisor
            val divider = View(this).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1
                )
                setBackgroundColor(getColor(R.color.background))
            }
            binding.llHistorico.addView(divider)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilitários
    // ─────────────────────────────────────────────────────────────────────────

    private fun atualizarHintEntrada() {
        if (convertendoRealParaBtc) {
            binding.tilEntrada.hint = getString(R.string.hint_valor_reais)
            binding.tilEntrada.prefixText = "R$ "
        } else {
            binding.tilEntrada.hint = getString(R.string.hint_valor_btc)
            binding.tilEntrada.prefixText = "₿ "
        }
    }
}

