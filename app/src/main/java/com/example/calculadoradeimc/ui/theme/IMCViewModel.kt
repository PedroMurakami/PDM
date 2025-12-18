package com.example.calculadoradeimc.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calculadoradeimc.data.Registro
import com.example.calculadoradeimc.data.RegistroDao
import com.example.calculadoradeimc.domain.CalculadoraSaude
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. Atualizamos o Estado para incluir as novas medidas
data class UiState(
    val peso: String = "",
    val altura: String = "",
    val idade: String = "",
    val sexo: String = "M",
    val fatorAtividade: Double = 1.2,

    // Novos campos
    val pescoco: String = "",
    val cintura: String = "",
    val quadril: String = "",

    val erro: String? = null,
    val resultadoSalvo: Registro? = null
)

class IMCViewModel(private val dao: RegistroDao) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    val historico = dao.listarTodos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Funções de Update Antigas
    fun atualizarPeso(v: String) { uiState = uiState.copy(peso = v, erro = null) }
    fun atualizarAltura(v: String) { uiState = uiState.copy(altura = v, erro = null) }
    fun atualizarIdade(v: String) { if (v.all { it.isDigit() }) uiState = uiState.copy(idade = v, erro = null) }
    fun selecionarSexo(v: String) { uiState = uiState.copy(sexo = v) }
    fun selecionarAtividade(v: Double) { uiState = uiState.copy(fatorAtividade = v) }

    // 2. Novas Funções de Update (ISSO VAI CORRIGIR O ERRO DA TELA)
    fun atualizarPescoco(v: String) { uiState = uiState.copy(pescoco = v) }
    fun atualizarCintura(v: String) { uiState = uiState.copy(cintura = v) }
    fun atualizarQuadril(v: String) { uiState = uiState.copy(quadril = v) }

    fun calcularESalvar() {
        // Converte strings para Double
        val p = uiState.peso.replace(",", ".").toDoubleOrNull()
        val a = uiState.altura.replace(",", ".").toDoubleOrNull()
        val i = uiState.idade.toIntOrNull()
        val pescoco = uiState.pescoco.replace(",", ".").toDoubleOrNull()
        val cintura = uiState.cintura.replace(",", ".").toDoubleOrNull()
        val quadril = uiState.quadril.replace(",", ".").toDoubleOrNull()

        if (p == null || a == null || i == null || p <= 0 || a <= 0) {
            uiState = uiState.copy(erro = "Preencha Peso, Altura e Idade corretamente.")
            return
        }

        // Chama o cálculo do Domínio
        val res = CalculadoraSaude.calcularResultadosCompletos(
            p, a, i, uiState.sexo, uiState.fatorAtividade,
            pescoco, cintura, quadril
        )

        // Cria o registro para salvar
        val reg = Registro(
            peso = p, altura = a, idade = i, sexo = uiState.sexo, fatorAtividade = uiState.fatorAtividade,
            circunferenciaPescoco = pescoco, circunferenciaCintura = cintura, circunferenciaQuadril = quadril,
            imc = res.imc, classificacaoImc = res.categoria.descricao,
            tmb = res.tmb, necessidadesCaloricas = res.gastoCaloricoDiario,
            pesoIdeal = res.pesoIdeal, percentualGordura = res.gorduraCorporalEstimada
        )

        viewModelScope.launch {
            dao.inserir(reg)
            uiState = uiState.copy(resultadoSalvo = reg, erro = null)
        }
    }

    fun deletarRegistro(r: Registro) { viewModelScope.launch { dao.deletar(r) } }
}

class IMCViewModelFactory(private val dao: RegistroDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IMCViewModel::class.java)) return IMCViewModel(dao) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}