package com.example.calculadoradeimc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // <-- CORREÇÃO DO ERRO DE IMPORT
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import com.example.calculadoradeimc.data.Registro

// -----------------------------------------------------------------------
// TELA DE CÁLCULO
// -----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCalculadora(viewModel: IMCViewModel, navController: NavController, tipoCalculo: String) {
    val state = viewModel.uiState
    val tituloTela = if (tipoCalculo == "completo") "Análise Completa" else "IMC Rápido"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(tituloTela, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardInputsAdaptavel(state, viewModel, tipoCalculo)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.calcularESalvar() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null)
                Spacer(Modifier.width(12.dp))
                Text("GERAR RESULTADOS", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (state.erro != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(state.erro!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                }
            }

            AnimatedVisibility(visible = state.resultadoSalvo != null) {
                state.resultadoSalvo?.let { registro ->
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Seu Relatório", fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(12.dp))
                    CardResultadoModerno(registro)
                }
            }
        }
    }
}

@Composable
fun CardInputsAdaptavel(s: UiState, vm: IMCViewModel, tipoCalculo: String) {
    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Dados Pessoais", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoEntrada(valor = s.peso, label = "Peso (kg)", icone = Icons.Default.Person, modifier = Modifier.weight(1f)) { vm.atualizarPeso(it) }
                CampoEntrada(valor = s.altura, label = "Altura (m)", icone = Icons.Default.Info, modifier = Modifier.weight(1f)) { vm.atualizarAltura(it) }
            }
            Spacer(Modifier.height(12.dp))
            CampoEntrada(valor = s.idade, label = "Idade", icone = Icons.Default.DateRange, imeAction = ImeAction.Done) { vm.atualizarIdade(it) }

            Spacer(Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))

            Text("Sexo Biológico", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SeletorSexo(selected = s.sexo == "M", label = "Homem", icon = Icons.Default.Male) { vm.selecionarSexo("M") }
                SeletorSexo(selected = s.sexo == "F", label = "Mulher", icon = Icons.Default.Female) { vm.selecionarSexo("F") }
            }

            // --- CORREÇÃO: Campos que davam erro "Unresolved reference" agora existem no ViewModel ---
            if (tipoCalculo == "completo") {
                Spacer(Modifier.height(24.dp))
                Text("Medidas (cm) - Para Gordura Corporal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoEntrada(valor = s.pescoco, label = "Pescoço", modifier = Modifier.weight(1f)) { vm.atualizarPescoco(it) }
                    CampoEntrada(valor = s.cintura, label = "Cintura", modifier = Modifier.weight(1f)) { vm.atualizarCintura(it) }
                }
                if (s.sexo == "F") {
                    Spacer(Modifier.height(12.dp))
                    CampoEntrada(valor = s.quadril, label = "Quadril") { vm.atualizarQuadril(it) }
                }
            }
            // ---------------------------------------------------

            Spacer(Modifier.height(24.dp))
            Text("Nível de Atividade Física", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(8.dp)) {
                    listOf(1.2 to "Sedentário", 1.375 to "Leve", 1.55 to "Moderado", 1.725 to "Intenso").forEach { (v, t) ->
                        OpcaoAtividade(t, s.fatorAtividade == v) { vm.selecionarAtividade(v) }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoEntrada(valor: String, label: String, icone: ImageVector? = null, modifier: Modifier = Modifier, imeAction: ImeAction = ImeAction.Next, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor, onValueChange = onValueChange, label = { Text(label) }, modifier = modifier.fillMaxWidth(),
        leadingIcon = if (icone != null) { { Icon(icone, null, tint = MaterialTheme.colorScheme.primary) } } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
        shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorSexo(selected: Boolean, label: String, icon: ImageVector, onClick: () -> Unit) {
    FilterChip(
        selected = selected, onClick = onClick, label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer),
        shape = RoundedCornerShape(50)
    )
}

@Composable
fun OpcaoAtividade(texto: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() }.background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
        Spacer(Modifier.width(8.dp))
        Text(texto, fontSize = 14.sp)
    }
}

@Composable
fun CardResultadoModerno(r: Registro) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(24.dp).fillMaxWidth()) {
            Text("IMC Principal", color = Color.White.copy(alpha = 0.8f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${r.imc}", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(r.classificacaoImc, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
            Divider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ItemResultadoMini("Peso Ideal", "${r.pesoIdeal} kg", Icons.Default.FitnessCenter)
                ItemResultadoMini("Gasto Diário", "${r.necessidadesCaloricas.toInt()} kcal", Icons.Default.LocalFireDepartment)
            }
            r.percentualGordura?.let {
                Spacer(Modifier.height(20.dp))
                ItemResultadoMini("Gordura Estimada", "$it %", Icons.Default.Opacity)
            }
        }
    }
}

@Composable
fun ItemResultadoMini(titulo: String, valor: String, icone: ImageVector) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icone, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(titulo, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// -----------------------------------------------------------------------
// TELA HISTÓRICO
// -----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHistorico(viewModel: IMCViewModel, navController: NavController) {
    val listaRegistros by viewModel.historico.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seu Histórico", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (listaRegistros.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Nenhum registro ainda.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(padding)) {
                items(listaRegistros) { item ->
                    ItemHistoricoModerno(item, onDelete = { viewModel.deletarRegistro(item) })
                }
            }
        }
    }
}

@Composable
fun ItemHistoricoModerno(registro: Registro, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val temGordura = registro.percentualGordura != null

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(sdf.format(Date(registro.dataHora)).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, "Excluir", tint = Color.Gray)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("IMC ${registro.imc}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Text(registro.classificacaoImc, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Peso Ideal", fontSize = 12.sp, color = Color.Gray)
                    Text("${registro.pesoIdeal} kg", fontWeight = FontWeight.Bold)
                }
                if (temGordura) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Gordura", fontSize = 12.sp, color = Color.Gray)
                        Text("${registro.percentualGordura}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                } else {
                    Text("Cálculo Rápido", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }
    }
}