package com.example.calculadoradeimc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calculadoradeimc.data.AppDatabase
import com.example.calculadoradeimc.ui.IMCViewModel
import com.example.calculadoradeimc.ui.IMCViewModelFactory
import com.example.calculadoradeimc.ui.TelaCalculadora
import com.example.calculadoradeimc.ui.TelaHistorico
import com.example.calculadoradeimc.ui.TelaMenu

// --- NOVAS CORES MODERNAS (Tons de Menta/Teal) ---
val MintPrimary = Color(0xFF00A896)      // Verde principal vibrante
val MintSecondary = Color(0xFF028090)    // Um tom mais azulado para contraste
val MintContainer = Color(0xFFE0F2F1)    // Fundo muito claro, quase branco-menta
val MintBackground = Color(0xFFF8F9FA)   // Fundo geral limpo (off-white)
val DarkText = Color(0xFF1A1C1E)         // Texto escuro para leitura

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Configuração do Tema com as novas cores
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = MintPrimary,
                    onPrimary = Color.White,
                    primaryContainer = MintContainer,
                    onPrimaryContainer = DarkText,
                    background = MintBackground,
                    surface = Color.White,
                    onSurface = DarkText
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Inicialização do Banco e ViewModel
    val db = AppDatabase.getDatabase(context)
    val viewModel: IMCViewModel = viewModel(
        factory = IMCViewModelFactory(db.registroDao())
    )

    // Navegação atualizada
    NavHost(navController = navController, startDestination = "menu") {

        // ROTA 1: O Novo Menu Principal
        composable("menu") {
            TelaMenu(navController = navController)
        }

        // ROTA 2: A Tela de Cálculo (que se adapta)
        // Ela recebe um argumento "tipo" (basico ou completo)
        composable(
            route = "calcular/{tipo}",
            arguments = listOf(navArgument("tipo") { type = NavType.StringType })
        ) { backStackEntry ->
            val tipoCalculo = backStackEntry.arguments?.getString("tipo") ?: "basico"
            TelaCalculadora(viewModel = viewModel, navController = navController, tipoCalculo = tipoCalculo)
        }

        // ROTA 3: Histórico
        composable("historico") {
            TelaHistorico(viewModel = viewModel, navController = navController)
        }
    }
}