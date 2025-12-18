package com.example.calculadoradeimc.domain

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

// Enum para as faixas de peso
enum class CategoriaIMC(val descricao: String) {
    ABAIXO_DO_PESO("Abaixo do peso"),
    NORMAL("Peso normal"),
    SOBREPESO("Sobrepeso"),
    OBESIDADE_GRAU_I("Obesidade Grau I"),
    OBESIDADE_GRAU_II("Obesidade Grau II"),
    OBESIDADE_GRAU_III("Obesidade Grau III (Mórbida)")
}

// Classe que agrupa todos os resultados
data class ResultadoSaude(
    val imc: Double,
    val categoria: CategoriaIMC,
    val tmb: Double,                 // Taxa Metabólica Basal
    val gastoCaloricoDiario: Double, // Calorias para manter o peso
    val pesoIdeal: Double,
    val gorduraCorporalEstimada: Double? // Pode ser nulo se não tiver medidas
)

object CalculadoraSaude {

    // Função auxiliar para arredondar números (ex: 23.456 -> 23.5)
    private fun arredondar(valor: Double, casas: Int): Double {
        var multiplicador = 1.0
        repeat(casas) { multiplicador *= 10 }
        return (valor * multiplicador).roundToInt() / multiplicador
    }

    /**
     * Função principal que decide quais cálculos fazer baseada nos dados disponíveis.
     * Os parâmetros 'pescoco', 'cintura' e 'quadril' são opcionais (nullable).
     */
    fun calcularResultadosCompletos(
        peso: Double,
        altura: Double,
        idade: Int,
        sexo: String,
        fatorAtividade: Double,
        pescoco: Double? = null, // Em cm
        cintura: Double? = null, // Em cm
        quadril: Double? = null  // Em cm (obrigatório para mulheres na fórmula da Marinha)
    ): ResultadoSaude {

        // Validação básica de segurança
        if (altura <= 0.0 || peso <= 0.0) {
            return ResultadoSaude(0.0, CategoriaIMC.ABAIXO_DO_PESO, 0.0, 0.0, 0.0, 0.0)
        }

        // --- 1. IMC (Peso / Altura²) ---
        val imc = arredondar(peso / altura.pow(2), 2)

        val categoria = when {
            imc < 18.5 -> CategoriaIMC.ABAIXO_DO_PESO
            imc < 25.0 -> CategoriaIMC.NORMAL
            imc < 30.0 -> CategoriaIMC.SOBREPESO
            imc < 35.0 -> CategoriaIMC.OBESIDADE_GRAU_I
            imc < 40.0 -> CategoriaIMC.OBESIDADE_GRAU_II
            else -> CategoriaIMC.OBESIDADE_GRAU_III
        }

        // --- 2. TMB (Fórmula de Mifflin-St Jeor) ---
        // Homens: (10 x peso) + (6.25 x altura_cm) - (5 x idade) + 5
        // Mulheres: (10 x peso) + (6.25 x altura_cm) - (5 x idade) - 161
        val alturaCm = altura * 100
        val baseTmb = (10 * peso) + (6.25 * alturaCm) - (5 * idade)
        val tmb = if (sexo.equals("M", ignoreCase = true)) {
            arredondar(baseTmb + 5, 0)
        } else {
            arredondar(baseTmb - 161, 0)
        }

        // --- 3. Necessidade Calórica Diária ---
        val gastoCalorico = arredondar(tmb * fatorAtividade, 0)

        // --- 4. Peso Ideal (Fórmula de Devine) ---
        // Homens: 50kg + 2.3kg por polegada acima de 5 pés
        // Mulheres: 45.5kg + 2.3kg por polegada acima de 5 pés
        val alturaPol = alturaCm / 2.54
        val polegadasAcima = alturaPol - 60 // 5 pés = 60 polegadas

        val pesoBase = if (sexo.equals("M", ignoreCase = true)) 50.0 else 45.5
        val pesoIdealCalculado = if (polegadasAcima > 0) {
            pesoBase + (2.3 * polegadasAcima)
        } else {
            pesoBase // Se for muito baixo, mantém o base
        }
        val pesoIdeal = arredondar(pesoIdealCalculado, 1)

        // --- 5. Gordura Corporal (Fórmula da Marinha Americana) ---
        // Requer medidas em CM e LOGARITMOS (log10)
        var gordura: Double? = null

        // Só calcula se tivermos as medidas necessárias
        if (pescoco != null && cintura != null && pescoco > 0 && cintura > 0) {

            val logAltura = log10(alturaCm)

            if (sexo.equals("M", ignoreCase = true)) {
                // Fórmula Homens: 495 / (1.0324 - 0.19077(log10(cintura - pescoco)) + 0.15456(log10(altura))) - 450
                if (cintura - pescoco > 0) { // Previne log de número negativo
                    val logCinturaPescoco = log10(cintura - pescoco)
                    val densidade = 1.0324 - (0.19077 * logCinturaPescoco) + (0.15456 * logAltura)
                    gordura = arredondar((495 / densidade) - 450, 1)
                }
            } else {
                // Fórmula Mulheres: Precisa do quadril também
                // 495 / (1.29579 - 0.35004(log10(cintura + quadril - pescoco)) + 0.22100(log10(altura))) - 450
                if (quadril != null && quadril > 0 && (cintura + quadril - pescoco) > 0) {
                    val logMedidas = log10(cintura + quadril - pescoco)
                    val densidade = 1.29579 - (0.35004 * logMedidas) + (0.22100 * logAltura)
                    gordura = arredondar((495 / densidade) - 450, 1)
                }
            }
        }

        // Se a fórmula da Marinha falhar (falta de dados) e quisermos um fallback,
        // poderíamos usar o IMC aqui, mas para este requisito vamos retornar null
        // para indicar que precisa das medidas exatas.

        return ResultadoSaude(
            imc = imc,
            categoria = categoria,
            tmb = tmb,
            gastoCaloricoDiario = gastoCalorico,
            pesoIdeal = pesoIdeal,
            gorduraCorporalEstimada = gordura
        )
    }
}