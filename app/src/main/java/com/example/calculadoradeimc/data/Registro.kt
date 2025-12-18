package com.example.calculadoradeimc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_tabela")
data class Registro(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val dataHora: Long = System.currentTimeMillis(),

    // --- Inputs Básicos ---
    val peso: Double,
    val altura: Double,
    val idade: Int,
    val sexo: String, // "M" ou "F"
    val fatorAtividade: Double,

    // --- Novos Inputs para Gordura Corporal (Opcionais) ---
    // Em centímetros. Podem ser nulos se o usuário não preencher.
    val circunferenciaPescoco: Double? = null,
    val circunferenciaCintura: Double? = null,
    val circunferenciaQuadril: Double? = null, // Necessário apenas para mulheres

    // --- Resultados Calculados ---
    val imc: Double,
    val classificacaoImc: String,

    // Taxa Metabólica Basal (Mifflin-St Jeor)
    val tmb: Double,

    // Necessidade Calórica Diária (TMB * Fator Atividade)
    val necessidadesCaloricas: Double,

    // Peso Ideal (Fórmula de Devine)
    val pesoIdeal: Double,

    // Percentual de Gordura (Fórmula da Marinha Americana)
    val percentualGordura: Double?
)