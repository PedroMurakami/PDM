package com.example.calculadoradeimc.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: Registro)

    @Delete
    suspend fun deletar(registro: Registro)

    @Query("SELECT * FROM registros_tabela ORDER BY dataHora DESC")
    fun listarTodos(): Flow<List<Registro>>
}