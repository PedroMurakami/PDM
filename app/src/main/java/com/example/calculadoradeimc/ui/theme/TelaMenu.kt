package com.example.calculadoradeimc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TelaMenu(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Cabeçalho de Boas-vindas
        Text("Olá!", fontSize = 28.sp, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onSurface)
        Text(
            "Vamos cuidar da sua saúde hoje?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Seção de Cartões Principais
        Text("Escolha um cálculo", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // Cartão 1: IMC Rápido (Navega para calcular/basico)
        MenuCard(
            titulo = "IMC Rápido",
            descricao = "Apenas peso e altura para uma verificação simples.",
            icone = Icons.Default.Speed,
            corGradiente = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary),
            onClick = { navController.navigate("calcular/basico") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cartão 2: Completo (Navega para calcular/completo)
        MenuCard(
            titulo = "Análise Corporal Completa",
            descricao = "Inclui gordura corporal, TMB e peso ideal (requer fita métrica).",
            icone = Icons.Default.AccessibilityNew,
            corGradiente = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary),
            onClick = { navController.navigate("calcular/completo") }
        )

        Spacer(modifier = Modifier.weight(1f)) // Empurra o resto para baixo

        // Botão de Histórico na parte inferior
        OutlinedButton(
            onClick = { navController.navigate("historico") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.History, null)
            Spacer(Modifier.width(8.dp))
            Text("VER MEU HISTÓRICO", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Componente de Cartão Personalizado e Moderno
@Composable
fun MenuCard(
    titulo: String,
    descricao: String,
    icone: ImageVector,
    corGradiente: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp), // Cantos bem arredondados
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra suave
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(corGradiente)) // Fundo gradiente
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone dentro de um círculo branco
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icone, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text(descricao, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 18.sp)
                }
            }
        }
    }
}